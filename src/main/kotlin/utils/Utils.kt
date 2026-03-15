package ru.utilityorders.backend.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.mkammerer.argon2.Argon2
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import org.jetbrains.exposed.v1.core.CustomFunction
import org.jetbrains.exposed.v1.core.Expression
import org.jetbrains.exposed.v1.core.TextColumnType
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.stringLiteral
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import ru.utilityorders.backend.entities.db.OrderDB
import ru.utilityorders.backend.entities.db.WorkerDB
import ru.utilityorders.backend.entities.serial.Reason
import java.util.Date
import kotlin.reflect.full.primaryConstructor
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import java.time.LocalDate as JavaLocalDate

private val currentDate = JavaLocalDate.now()!!

private val config = HikariConfig().apply {
    driverClassName = "org.postgresql.Driver"
    jdbcUrl = System.getenv("POSTGRES_URL")
    username = System.getenv("POSTGRES_USER")
    password = System.getenv("POSTGRES_PASSWORD")
}
private val datasource = HikariDataSource(config)
private val connect = Database.connect(datasource)

suspend fun <T> suspendTransaction(block: suspend Transaction.() -> T) =
    suspendTransaction(db = connect, statement = block)

fun generateUserToken() =
    (1..50).map {
        (('A'..'Z') + ('a'..'z') + ('0'..'9')).random()
    }.joinToString()

fun <T> Iterable<T>.joinToString() = joinToString("")

fun createAccessJWT(subject: String, jwtSecret: String) =
    JWT.create()
        .withSubject(subject)
        .withClaim("type", "access")
        .withExpiresAt(Date(System.currentTimeMillis() + 600_000))
        .sign(Algorithm.HMAC256(jwtSecret))!!

fun createRefreshJWT(subject: String, jwtSecret: String) =
    JWT.create()
        .withSubject(subject)
        .withClaim("type", "refresh")
        .withExpiresAt(Date(System.currentTimeMillis() + 43_200_000))
        .sign(Algorithm.HMAC256(jwtSecret))!!

fun Argon2.passwordToHash(password: String) =
    hash(3, 64 * 1024, 1, password.toCharArray())!!

fun Argon2.checkingPassword(hash: String, password: String) =
    verify(hash, password.toCharArray())

@OptIn(ExperimentalUuidApi::class)
fun List<OrderDB>.workerWithMinOrdersOnDate(workers: List<WorkerDB>, date: JavaLocalDate) =
    workers
        .map { worker ->
            val count = count {
                it.workerID == worker.id && it.executionAt.toString() == date.toString()
            }
            worker to count
        }
        .filter { it.second < it.first.limit }
        .minByOrNull { it.second }
        ?.first?.id

@OptIn(ExperimentalUuidApi::class)
fun List<OrderDB>.findNextAvailableSlot(
    workers: List<WorkerDB>,
    startDate: JavaLocalDate = currentDate
): Pair<JavaLocalDate, Uuid> {

    var date = startDate

    while (true) {
        val workerID = workerWithMinOrdersOnDate(workers, date)

        if (workerID != null) return date to workerID
        date = date.plusDays(1)
    }
}

fun CharSequence.isValidEmail() =
    matches("[A-Za-z0-9+_.-]+@[A-Za-z0-9]+\\.[a-z]{2,}".toRegex())

fun Any.checkingEntityForEmptyFields(): List<String> {
    val reasonList = mutableListOf<String>()

    this::class.primaryConstructor?.parameters?.forEach {
        val name = it.name!!

        val value = this.javaClass.getParameterEntity(this, name)
        if (value.isBlank()) reasonList.add(name)
    }
    return reasonList
}

fun <T: Any> Class<T>.getParameterEntity(entity: T, name: String): String {
    val field = getDeclaredField(name)
    field.isAccessible = true

    return field.get(entity).toString()
}

fun List<String>.toReason() = map { Reason(it, INPUT_FIELD_EMPTY) }

fun CharSequence.isValidGender() =
    this == "Мужчина" || this == "Женщина"

fun CharSequence.isValidDate() =
    matches("[0-9]{4}+-[0-9]{2}+-[0-9]{2}".toRegex())

@OptIn(ExperimentalUuidApi::class)
suspend fun RoutingContext.wrapperCheckValidId(body: suspend (Uuid) -> Unit) {
    val id = call.principal<Uuid>()
        ?: return call.respond(HttpStatusCode.BadRequest, INCORRECT_ID)

    body(id)
}

@OptIn(ExperimentalUuidApi::class)
fun String?.toUuidOrNull() =
    this?.let { Uuid.parseOrNull(it) }

@OptIn(ExperimentalUuidApi::class)
inline fun <reified T: Any> Route.postWrapperCheckValidId(noinline body: suspend RoutingContext.(T, Uuid) -> Unit) =
    post<T> { resource ->
        wrapperCheckValidId { body(resource, it) }
    }

@OptIn(ExperimentalUuidApi::class)
inline fun <reified T: Any> Route.getWrapperCheckValidId(noinline body: suspend RoutingContext.(T, Uuid) -> Unit) =
    get<T> { resource ->
        wrapperCheckValidId { body(resource, it) }
    }

fun Collection<Reason>.doesFieldNotExist(field: String) = find { it.field == field } == null

fun concatWS(vararg expr: Expression<*>) = concatWS(" ", expr = expr)

fun concatWS(separator: String, vararg expr: Expression<*>) =
    CustomFunction(
        "CONCAT_WS",
        TextColumnType(),
        stringLiteral(separator),
        *expr
    )

suspend fun ApplicationCall.respondIncorrectID() = respond(HttpStatusCode.BadRequest, INCORRECT_ID)

suspend fun ApplicationCall.respondUserDoesNotExist() = respond(HttpStatusCode.BadRequest, USER_DOES_NOT_EXIST)

suspend fun ApplicationCall.respondReasons(list: List<Reason>) = respond(HttpStatusCode.BadRequest, list)

suspend fun ApplicationCall.respondNoContent() = respond(HttpStatusCode.NoContent)

suspend fun ApplicationCall.respondInputFieldEmpty() = respond(HttpStatusCode.BadRequest, INPUT_FIELD_EMPTY)