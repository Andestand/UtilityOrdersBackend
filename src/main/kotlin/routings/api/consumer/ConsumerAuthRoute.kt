package ru.utilityorders.backend.routings.api.consumer

import de.mkammerer.argon2.Argon2
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import ru.utilityorders.backend.database.consumer.ConsumerRepository
import ru.utilityorders.backend.entities.serial.ConsumerSignInForm
import ru.utilityorders.backend.entities.serial.ConsumerSignUpForm
import ru.utilityorders.backend.entities.serial.JwtResponse
import ru.utilityorders.backend.entities.serial.Reason
import ru.utilityorders.backend.resources.Consumer
import ru.utilityorders.backend.utils.EMAIL
import ru.utilityorders.backend.utils.INCORRECT_EMAIL
import ru.utilityorders.backend.utils.INCORRECT_EMAIL_FORMAT
import ru.utilityorders.backend.utils.JWT_SECRET
import ru.utilityorders.backend.utils.PASSWORD
import ru.utilityorders.backend.utils.USER_WITH_SUCH_EMAIL_EXISTS
import ru.utilityorders.backend.utils.WRONG_PASSWORD
import ru.utilityorders.backend.utils.checkingEntityForEmptyFields
import ru.utilityorders.backend.utils.checkingPassword
import ru.utilityorders.backend.utils.createAccessJWT
import ru.utilityorders.backend.utils.createRefreshJWT
import ru.utilityorders.backend.utils.doesFieldNotExist
import ru.utilityorders.backend.utils.isValidEmail
import ru.utilityorders.backend.utils.respondReasons
import ru.utilityorders.backend.utils.toDB
import ru.utilityorders.backend.utils.toReason
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Route.consumerAuthRoute(repository: ConsumerRepository, argon: Argon2) {
    val jwtSecret = environment.config.property(JWT_SECRET).getString()

    post<Consumer.SignIn> {
        val receive = call.receive<ConsumerSignInForm>()
        val reasonList = mutableListOf<Reason>()
        var uid: Uuid? = null

        receive.checkingEntityForEmptyFields().also {
            reasonList.addAll(it.toReason())
        }

        if (reasonList.doesFieldNotExist(EMAIL))
            if (receive.email.isValidEmail()) {
                val user = repository.findCredentialsByEmail(receive.email)

                if (user != null) {
                    if (!argon.checkingPassword(user.password, receive.password))
                        reasonList.add(Reason(PASSWORD, WRONG_PASSWORD))
                    else
                        uid = user.id
                } else
                    reasonList.add(Reason(EMAIL, INCORRECT_EMAIL))

            } else
                reasonList.add(Reason(EMAIL, INCORRECT_EMAIL_FORMAT))

        if (reasonList.isNotEmpty())
            return@post call.respondReasons(reasonList)

        call.respond(
            JwtResponse(
                createAccessJWT(uid.toString(), jwtSecret),
                createRefreshJWT(uid.toString(), jwtSecret)
            )
        )
    }

    post<Consumer.SignUp> {
        val receive = call.receive<ConsumerSignUpForm>()
        val reasonList = mutableListOf<Reason>()

        receive.checkingEntityForEmptyFields().also {
            reasonList.addAll(it.toReason())
        }

        if (reasonList.doesFieldNotExist(EMAIL))
            if (receive.email.isValidEmail()) {
                val doesUserExist = repository.findCredentialsByEmail(receive.email)

                if (doesUserExist != null)
                    reasonList.add(Reason(EMAIL, USER_WITH_SUCH_EMAIL_EXISTS))
            } else
                reasonList.add(Reason(EMAIL, INCORRECT_EMAIL_FORMAT))

        if (reasonList.isNotEmpty())
            return@post call.respondReasons(reasonList)

        val user = repository.addUser(receive.toDB())

        call.respond(
            JwtResponse(
                createAccessJWT(user.id.toString(), jwtSecret),
                createRefreshJWT(user.id.toString(), jwtSecret)
            )
        )
    }
}