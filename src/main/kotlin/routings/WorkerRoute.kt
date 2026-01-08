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
import ru.utilityorders.backend.entities.OrderFulfillmentStatus
import ru.utilityorders.backend.entities.ProfileResponse
import ru.utilityorders.backend.entities.ResultList
import ru.utilityorders.backend.entities.SignUpRequest
import ru.utilityorders.backend.resources.MeRes
import ru.utilityorders.backend.resources.Worker
import ru.utilityorders.backend.utils.INCORRECT_ID
import ru.utilityorders.backend.utils.ORDER_NOT_FOUND
import ru.utilityorders.backend.utils.USER_DOES_NOT_EXIST
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
        get<MeRes> {
            val uid = call.principal<UUID>()

            if (uid != null) {
                val user = workerRepository.findUserByUID(uid)

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
                else
                    call.respond(HttpStatusCode.BadRequest, Message(USER_DOES_NOT_EXIST))
            } else
                call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
        }

        get<MeRes.Orders> {
            val uid = call.principal<UUID>()

            if (uid != null) {
                val list = ordersRepository.getOrdersByWorkerID(uid).toSerial()
                call.respond(HttpStatusCode.OK, ResultList(list, list.size))
            } else
                call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
        }

        get<MeRes.Orders.Order> {
            val uid = call.principal<UUID>()

            if (uid != null)
                try {
                    val orderID = UUID.fromString(it.id)
                    val order = ordersRepository.findOrderByIdAndWorkerId(uid, orderID)

                    if (order != null)
                        call.respond(order.toSerial())
                    else
                        call.respond(HttpStatusCode.NotFound, Message(ORDER_NOT_FOUND))
                } catch (_: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
                }
            else
                call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
        }

        post<MeRes.Orders.Order.ProceedToOrder> {
            val uid = call.principal<UUID>()

            if (uid != null)
                try {
                    val orderID = UUID.fromString(it.parent.id)
                    val order = ordersRepository.findOrderByIdAndWorkerId(uid, orderID)

                    if (order != null) {
                        ordersRepository.setStatus(orderID, OrderFulfillmentStatus.IN_PROGRESS)
                        call.respond(HttpStatusCode.NoContent)
                    }
                    else
                        call.respond(HttpStatusCode.NotFound, Message(ORDER_NOT_FOUND))
                } catch (_: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
                }
            else
                call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
        }

        post<MeRes.Orders.Order.OrderCompleted> {
            val uid = call.principal<UUID>()

            if (uid != null)
                try {
                    val orderID = UUID.fromString(it.parent.id)
                    val order = ordersRepository.findOrderByIdAndWorkerId(uid, orderID)

                    if (order != null) {
                        ordersRepository.setStatus(orderID, OrderFulfillmentStatus.COMPLETED)
                        call.respond(HttpStatusCode.NoContent)
                    }
                    else
                        call.respond(HttpStatusCode.NotFound, Message(ORDER_NOT_FOUND))
                } catch (_: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
                }
            else
                call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
        }

        post<MeRes.Orders.Order.CancelOrder> {
            val uid = call.principal<UUID>()

            if (uid != null)
                try {
                    val orderID = UUID.fromString(it.parent.id)
                    val order = ordersRepository.findOrderByIdAndWorkerId(uid, orderID)

                    if (order != null) {
                        ordersRepository.setStatus(orderID, OrderFulfillmentStatus.CANCEL)
                        call.respond(HttpStatusCode.NoContent)
                    }
                    else
                        call.respond(HttpStatusCode.NotFound, Message(ORDER_NOT_FOUND))
                } catch (_: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
                }
            else
                call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
        }

        post<MeRes.Logout> {
            val uid = call.principal<UUID>()

            if (uid != null) {
                workerRepository.updateUserSecret(uid)
                call.respond(HttpStatusCode.NoContent)
            } else
                call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
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
            call.respond(HttpStatusCode.BadRequest, Message(USER_DOES_NOT_EXIST))
    }

    post<Worker.SignUp> {
        val receive = call.receive<SignUpRequest>()
        val token = workerRepository.addUser(receive.toDB())
        call.respond(token)
    }
}