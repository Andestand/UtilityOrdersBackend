package ru.utilityorders.backend.routings

import de.mkammerer.argon2.Argon2
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import ru.utilityorders.backend.core.JWT_SECRET
import ru.utilityorders.backend.database.orders.OrdersRepository
import ru.utilityorders.backend.database.worker.WorkerRepository
import ru.utilityorders.backend.entities.JwtResponse
import ru.utilityorders.backend.entities.Message
import ru.utilityorders.backend.entities.ProfileResponse
import ru.utilityorders.backend.entities.ResultList
import ru.utilityorders.backend.entities.SignUpRequest
import ru.utilityorders.backend.resources.WorkerMe
import ru.utilityorders.backend.resources.Worker
import ru.utilityorders.backend.utils.checkingPassword
import ru.utilityorders.backend.utils.createAccessJWT
import ru.utilityorders.backend.utils.toDB
import ru.utilityorders.backend.utils.toSerial
import java.util.UUID

fun Route.workerRoute(
    argon2: Argon2,
    workerRepository: WorkerRepository,
    ordersRepository: OrdersRepository
) {
    val jwtSecret = environment.config.property(JWT_SECRET).getString()

    authenticate {
        get<WorkerMe> {
            val uid = call.principal<String>()
            val user = workerRepository.findUserByUID(UUID.fromString(uid))

            if (user != null)
                call.respond(
                    HttpStatusCode.OK,
                    ProfileResponse(
                        id = user.id.toString(),
                        firstName = user.firstName,
                        lastName = user.lastName,
                        surname = user.surname,
                        dateOfBirth = user.dateOfBirth.toString(),
                        dateOfRegistration = user.dateOfRegistration.toString()
                    )
                )
        }

        get<WorkerMe.Orders> {
            val uid = call.principal<String>()

            val list = ordersRepository.getOrdersByUser(UUID.fromString(uid)).toSerial()

            call.respond(
                status = HttpStatusCode.OK,
                message = ResultList(list, list.size)
            )
        }

        get<WorkerMe.Orders.Order> {
            try {
                val id = UUID.fromString(it.id)


            } catch (_: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    Message("")
                )
            }
        }

        post<WorkerMe.Orders.Order.ProceedToOrder> {
            val uid = call.principal<String>()

            ordersRepository.atWork(
                uid = UUID.fromString(uid),
                orderID = UUID.fromString(it.parent.id),
                value = true
            )
            call.respond(HttpStatusCode.OK)
        }

        post<WorkerMe.Logout> {
            val uid = call.principal<String>()

            workerRepository.updateUserSecret(UUID.fromString(uid))
        }
    }

    post<Worker.SignIn> {
        val receive = call.receive<String>()
        val usersTokenList = workerRepository.usersTokenList()
        var currentUid: UUID? = null
        var currentUserSecret: String? = null

        usersTokenList.forEach {
            if (argon2.checkingPassword(it.userToken, receive)) {
                currentUid = it.id
                currentUserSecret = it.userSecret
                return@forEach
            }
        }

        if (currentUid != null && currentUserSecret != null)
            call.respond(
                HttpStatusCode.OK,
                JwtResponse(createAccessJWT(currentUid.toString(), currentUserSecret, jwtSecret))
            )
        else
            call.respond(
                HttpStatusCode.BadRequest,
                Message("Такого пользователя не существует.")
            )
    }

    post<Worker.SignUp> {
        val receive = call.receive<SignUpRequest>()
        val token = workerRepository.addUser(receive.toDB())
        call.respond(token)
    }
}