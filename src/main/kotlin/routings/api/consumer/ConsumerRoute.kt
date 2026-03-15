package ru.utilityorders.backend.routings.api.consumer

import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import kotlinx.datetime.toKotlinLocalDate
import ru.utilityorders.backend.database.consumer.ConsumerRepository
import ru.utilityorders.backend.database.orders.OrdersRepository
import ru.utilityorders.backend.database.residential_addresses.ResidentialAddressesRepository
import ru.utilityorders.backend.database.worker.WorkerRepository
import ru.utilityorders.backend.entities.serial.AddAddress
import ru.utilityorders.backend.entities.serial.CreateOrder
import ru.utilityorders.backend.entities.serial.Reason
import ru.utilityorders.backend.resources.ConsumerMeRes
import ru.utilityorders.backend.utils.checkingEntityForEmptyFields
import ru.utilityorders.backend.utils.findNextAvailableSlot
import ru.utilityorders.backend.utils.getWrapperCheckValidId
import ru.utilityorders.backend.utils.postWrapperCheckValidId
import ru.utilityorders.backend.utils.respondIncorrectID
import ru.utilityorders.backend.utils.respondInputFieldEmpty
import ru.utilityorders.backend.utils.respondNoContent
import ru.utilityorders.backend.utils.respondReasons
import ru.utilityorders.backend.utils.toDB
import ru.utilityorders.backend.utils.toReason
import ru.utilityorders.backend.utils.toAddressSerial
import ru.utilityorders.backend.utils.toUuidOrNull
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Route.consumerRoute(
    consumerRepository: ConsumerRepository,
    ordersRepository: OrdersRepository,
    workerRepository: WorkerRepository,
    addressesRepository: ResidentialAddressesRepository
) {
    authenticate("access") {
        postWrapperCheckValidId<ConsumerMeRes.CreateOrder>  { _, uid ->
            val receive = call.receive<CreateOrder>()

            if (receive.header.isBlank())
                return@postWrapperCheckValidId call.respondInputFieldEmpty()

            val workers = workerRepository.workerList()
            val (executionAt, workerID) = ordersRepository.orderList()
                .findNextAvailableSlot(workers)

            ordersRepository.addOrder(
                header = receive.header,
                consumerID = uid,
                workerID = workerID,
                costOfWork = receive.costOfWork.toBigDecimal(),
                executionAt = executionAt.toKotlinLocalDate()
            )
            call.respondNoContent()
        }

        postWrapperCheckValidId<ConsumerMeRes.AddAddress> { _, uid ->
            val receive = call.receive<AddAddress>()
            val reasonList = mutableListOf<Reason>()

            receive.checkingEntityForEmptyFields().also {
                reasonList.addAll(it.toReason())
            }

            if (reasonList.isNotEmpty())
                return@postWrapperCheckValidId call.respondReasons(reasonList)

            val valueDB = receive.toDB(uid)

            addressesRepository.addAddress(valueDB)

            call.respondNoContent()
        }

        getWrapperCheckValidId<ConsumerMeRes.Addresses> { _, uid ->
            val list = addressesRepository.fetchAddressesByUID(uid)
            call.respond(list.toAddressSerial())
        }

        postWrapperCheckValidId<ConsumerMeRes.AddressDefault> { _, uid ->
            val addressID = call.receiveText().toUuidOrNull()
                ?: return@postWrapperCheckValidId call.respondIncorrectID()

            consumerRepository.setAddressDefault(uid, addressID)

            call.respondNoContent()
        }
    }
}