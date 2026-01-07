package ru.utilityorders.backend.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.mkammerer.argon2.Argon2
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.utilityorders.backend.database.consumer.ConsumerDAO
import ru.utilityorders.backend.database.entities.AddConsumerDB
import ru.utilityorders.backend.database.entities.AddWorkerDB
import ru.utilityorders.backend.database.entities.ConsumerDB
import ru.utilityorders.backend.database.entities.OrderDB
import ru.utilityorders.backend.database.entities.WorkerDB
import ru.utilityorders.backend.database.orders.OrdersDAO
import ru.utilityorders.backend.database.worker.WorkerDAO
import ru.utilityorders.backend.entities.ConsumerSignUpRequest
import ru.utilityorders.backend.entities.Order
import ru.utilityorders.backend.entities.SignUpRequest
import java.util.UUID
import java.time.LocalDate as JavaLocalDate

private val currentDate = JavaLocalDate.now()!!

suspend fun <T> suspendTransaction(body: suspend Transaction.() -> T) =
    newSuspendedTransaction(
        Dispatchers.IO,
        Database.connect(
            url = System.getenv("POSTGRES_URL"),
            user = System.getenv("POSTGRES_USER"),
            password = System.getenv("POSTGRES_PASSWORD")
        ),
        statement = body
    )

fun OrdersDAO.toDB() =
    OrderDB(
        id = id.value,
        header = header,
        consumerID = consumerID,
        workerID = workerID,
        costOfWork = costOfWork,
        address = address,
        atWork = atWork,
        dateOfAdded = dateOfAdded,
        executionDate = executionDate
    )

fun SignUpRequest.toDB() =
    AddWorkerDB(
        firstName = firstName,
        lastName = lastName,
        surname = surname,
        gender = gender,
        gettingStarted = LocalDate.parse(gettingStarted),
        dateOfBirth = LocalDate.parse(dateOfBirth)
    )

fun ConsumerSignUpRequest.toDB() = AddConsumerDB(firstName, email, password)

fun List<OrdersDAO>.toDB() = map { it.toDB() }

fun OrderDB.toSerial() =
    Order(
        id = id.toString(),
        header = header,
        consumerID = consumerID.toString(),
        workerID = workerID.toString(),
        costOfWork = costOfWork.toLong(),
        address = address,
        atWork = atWork,
        dateOfAdded = dateOfAdded.toString()
    )

fun ConsumerDAO.toDB() =
    ConsumerDB(
        id = id.value,
        firstName = firstName,
        email = email,
        address = address,
        dateOfRegistration = dateOfRegistration
    )

fun List<OrderDB>.toSerial() = map { it.toSerial() }

fun generateUserToken() =
    (1..50).map {
        (('A'..'Z') + ('a'..'z') + ('0'..'9')).random()
    }.joinToString()

fun <T> Iterable<T>.joinToString() = joinToString("")

fun createAccessJWT(subject: String, userSecret: String, jwtSecret: String) =
    JWT.create()
        .withSubject(subject)
        .withClaim("userSecret", userSecret)
        .withClaim("type", "access")
        .sign(Algorithm.HMAC256(jwtSecret))!!

fun createRefreshJWT(subject: String, secret: String) =
    JWT.create()
        .withSubject(subject)
        .withClaim("type", "refresh")
        .sign(Algorithm.HMAC256(secret))!!

fun WorkerDAO.toDB() =
    WorkerDB(
        id = id.value,
        firstName = firstName,
        lastName = lastName,
        surname = surname,
        dateOfBirth = dateOfBirth,
        limit = orderLimit,
        dateOfRegistration = dateOfRegistration
    )

fun Argon2.passwordToHash(password: String) =
    hash(3, 64 * 1024, 1, password.toCharArray())!!

fun Argon2.checkingPassword(hash: String, password: String) =
    verify(hash, password.toCharArray())

fun List<OrderDB>.workerWithMinOrdersOnDate(workers: List<WorkerDB>, date: JavaLocalDate) =
    workers
        .map { worker ->
            val count = count {
                it.workerID == worker.id && it.executionDate.toString() == date.toString()
            }
            worker to count
        }
        .filter { it.second < it.first.limit }
        .minByOrNull { it.second }
        ?.first?.id

fun List<OrderDB>.findNextAvailableSlot(
    workers: List<WorkerDB>,
    startDate: JavaLocalDate = currentDate
): Pair<JavaLocalDate, UUID> {

    var date = startDate

    while (true) {
        val workerID = workerWithMinOrdersOnDate(workers, date)

        if (workerID != null) return date to workerID
        date = date.plusDays(1)
    }
}