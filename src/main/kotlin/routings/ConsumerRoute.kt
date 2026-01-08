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
import kotlinx.datetime.toKotlinLocalDate
import ru.utilityorders.backend.core.JWT_SECRET
import ru.utilityorders.backend.database.consumer.ConsumerRepository
import ru.utilityorders.backend.database.orders.OrdersRepository
import ru.utilityorders.backend.database.worker.WorkerRepository
import ru.utilityorders.backend.entities.ConsumerProfileResponse
import ru.utilityorders.backend.entities.ConsumerSignInRequest
import ru.utilityorders.backend.entities.ConsumerSignUpRequest
import ru.utilityorders.backend.entities.CreateOrder
import ru.utilityorders.backend.entities.JwtResponse
import ru.utilityorders.backend.entities.Message
import ru.utilityorders.backend.entities.ResultList
import ru.utilityorders.backend.resources.Consumer
import ru.utilityorders.backend.resources.MeRes
import ru.utilityorders.backend.utils.INCORRECT_ID
import ru.utilityorders.backend.utils.ORDER_NOT_FOUND
import ru.utilityorders.backend.utils.USER_DOES_NOT_EXIST
import ru.utilityorders.backend.utils.checkingPassword
import ru.utilityorders.backend.utils.createAccessJWT
import ru.utilityorders.backend.utils.findNextAvailableSlot
import ru.utilityorders.backend.utils.toDB
import ru.utilityorders.backend.utils.toSerial
import java.util.UUID

fun Route.consumerRoute(
    argon2: Argon2,
    consumerRepository: ConsumerRepository,
    ordersRepository: OrdersRepository,
    workerRepository: WorkerRepository
) {
    val jwtSecret = environment.config.property(JWT_SECRET).getString()

    authenticate {
        get<MeRes> {
            val uid = call.principal<UUID>()

            if (uid != null) {
                val user = consumerRepository.findUserByID(uid)

                if (user != null)
                    call.respond(
                        HttpStatusCode.OK,
                        ConsumerProfileResponse(
                            id = user.id.toString(),
                            firstName = user.firstName,
                            email = user.email,
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
                val list = ordersRepository.getOrdersByConsumerID(uid).toSerial()
                call.respond(HttpStatusCode.OK, ResultList(list, list.size))
            } else
                call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
        }

        get<MeRes.Orders.Order> {
            val uid = call.principal<UUID>()

            if (uid != null)
                try {
                    val id = UUID.fromString(it.id)
                    val order = ordersRepository.findOrderByIdAndConsumerId(uid, id)

                    if (order != null)
                        call.respond(HttpStatusCode.OK, order.toSerial())
                    else
                        call.respond(HttpStatusCode.NotFound, Message(ORDER_NOT_FOUND))

                } catch (_: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
                }
            else
                call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
        }

        post<MeRes.CreateOrder> {
            val consumerID = call.principal<UUID>()
            val receive = call.receive<CreateOrder>()

            if (consumerID != null) {
                val consumer = consumerRepository.findUserByID(consumerID)

                val orderList = ordersRepository.orderList()
                val workerList = workerRepository.workerList()

                val (date, workerID) = orderList.findNextAvailableSlot(workerList)

                if (consumer != null && consumer.address != null) {
                    ordersRepository.addOrder(
                        header = receive.header,
                        consumerID = consumerID,
                        workerID = workerID,
                        costOfWork = receive.costOfWork.toBigDecimal(),
                        address = consumer.address,
                        executionDate = date.toKotlinLocalDate()
                    )
                    call.respond(HttpStatusCode.NoContent)
                }
            } else
                call.respond(HttpStatusCode.BadRequest, Message(INCORRECT_ID))
        }
    }

    post<Consumer.SignIn> {
        val receive = call.receive<ConsumerSignInRequest>()
        val credentials = consumerRepository.usersCredentialsList()

        var currentUid: UUID? = null
        var currentUserSecret: String? = null

        credentials.forEach {
            if (argon2.checkingPassword(it.password, receive.password)) {
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

    post<Consumer.SignUp> {
        val receive = call.receive<ConsumerSignUpRequest>()
        consumerRepository.addUser(receive.toDB())
        call.respond(HttpStatusCode.NoContent)
    }
}