package ru.utilityorders.backend.routings.api.worker

import de.mkammerer.argon2.Argon2
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import ru.utilityorders.backend.database.worker.WorkerRepository
import ru.utilityorders.backend.entities.serial.JwtResponse
import ru.utilityorders.backend.entities.serial.Reason
import ru.utilityorders.backend.entities.serial.WorkerSignUpForm
import ru.utilityorders.backend.entities.serial.WorkerSuccessAuthResponse
import ru.utilityorders.backend.resources.Worker
import ru.utilityorders.backend.utils.DATE_BIRTH
import ru.utilityorders.backend.utils.GENDER
import ru.utilityorders.backend.utils.GETTING_STARTED
import ru.utilityorders.backend.utils.INCORRECT_DATE_FORMAT
import ru.utilityorders.backend.utils.INCORRECT_GENDER_FORMAT
import ru.utilityorders.backend.utils.JWT_SECRET
import ru.utilityorders.backend.utils.checkingEntityForEmptyFields
import ru.utilityorders.backend.utils.checkingPassword
import ru.utilityorders.backend.utils.createAccessJWT
import ru.utilityorders.backend.utils.createRefreshJWT
import ru.utilityorders.backend.utils.isValidDate
import ru.utilityorders.backend.utils.isValidGender
import ru.utilityorders.backend.utils.respondReasons
import ru.utilityorders.backend.utils.respondUserDoesNotExist
import ru.utilityorders.backend.utils.toDB
import ru.utilityorders.backend.utils.toReason
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Route.workerAuthRoute(repository: WorkerRepository, argon: Argon2) {
    val jwtSecret = environment.config.property(JWT_SECRET).getString()

    post<Worker.SignIn> {
        val receive = call.receiveText()
        val tokens = repository.usersTokenList()
        var currentUser: Pair<Uuid, String>? = null

        tokens.forEach {
            if (argon.checkingPassword(it.second, receive)) {
                currentUser = it
                return@forEach
            }
        }

        if (currentUser == null)
            return@post call.respondUserDoesNotExist()

        call.respond(
            JwtResponse(
                createAccessJWT(currentUser.first.toString(), jwtSecret),
                createRefreshJWT(currentUser.first.toString(), jwtSecret)
            )
        )
    }

    post<Worker.SignUp> {
        val receive = call.receive<WorkerSignUpForm>()
        val reasonList = mutableListOf<Reason>()

        receive.checkingEntityForEmptyFields().also {
            reasonList.addAll(it.toReason())
        }

        if (!receive.gender.isValidGender())
            reasonList.add(Reason(GENDER, INCORRECT_GENDER_FORMAT))

        if (!receive.gettingStarted.isValidDate())
            reasonList.add(Reason(GETTING_STARTED, INCORRECT_DATE_FORMAT))

        if (!receive.dateBirth.isValidDate())
            reasonList.add(Reason(DATE_BIRTH, INCORRECT_DATE_FORMAT))

        if (reasonList.isNotEmpty())
            return@post call.respondReasons(reasonList)

        val newUser = repository.addUser(receive.toDB())

        call.respond(
            WorkerSuccessAuthResponse(
                token = newUser.second,
                jwt = JwtResponse(
                    createAccessJWT(newUser.first.toString(), jwtSecret),
                    createRefreshJWT(newUser.first.toString(), jwtSecret)
                )
            )
        )
    }
}