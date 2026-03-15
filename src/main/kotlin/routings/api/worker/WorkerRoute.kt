package ru.utilityorders.backend.routings.api.worker

import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import ru.utilityorders.backend.database.orders.OrdersRepository
import ru.utilityorders.backend.database.worker.WorkerRepository
import ru.utilityorders.backend.resources.MeRes
import ru.utilityorders.backend.utils.getWrapperCheckValidId
import ru.utilityorders.backend.utils.postWrapperCheckValidId
import ru.utilityorders.backend.utils.respondUserDoesNotExist
import ru.utilityorders.backend.utils.toOrderSerial
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Route.workerRoute(
    workerRepository: WorkerRepository,
    ordersRepository: OrdersRepository
) {
    authenticate("access") {
        getWrapperCheckValidId<MeRes> { _, uid ->
            val user = workerRepository.findUserByUID(uid)
                ?: return@getWrapperCheckValidId call.respondUserDoesNotExist()

            call.respond(user.toOrderSerial())
        }

        getWrapperCheckValidId<MeRes.Orders> { _, uid ->
            val list = ordersRepository.getOrdersByUID(uid)
            call.respond(list.toOrderSerial())
        }

        postWrapperCheckValidId<MeRes.Orders.Order.Completed> { parent, uuid ->
            val orderID = parent.parent.id


        }
    }
}