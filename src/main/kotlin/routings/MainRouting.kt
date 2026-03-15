package ru.utilityorders.backend.routings

import de.mkammerer.argon2.Argon2
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.utilityorders.backend.database.consumer.ConsumerRepository
import ru.utilityorders.backend.database.orders.OrdersRepository
import ru.utilityorders.backend.database.residential_addresses.ResidentialAddressesRepository
import ru.utilityorders.backend.database.worker.WorkerRepository
import ru.utilityorders.backend.routings.api.consumer.consumerAuthRoute
import ru.utilityorders.backend.routings.api.consumer.consumerRoute
import ru.utilityorders.backend.routings.api.utilsRoute
import ru.utilityorders.backend.routings.api.worker.workerAuthRoute
import ru.utilityorders.backend.routings.api.worker.workerRoute

fun Application.mainRouting() {
    val argon2 by inject<Argon2>()
    val workerRepository by inject<WorkerRepository>()
    val ordersRepository by inject<OrdersRepository>()
    val consumerRepository by inject<ConsumerRepository>()
    val addressesRepository by inject<ResidentialAddressesRepository>()

    routing {
        workerAuthRoute(
            repository = workerRepository,
            argon = argon2
        )
        consumerAuthRoute(
            repository = consumerRepository,
            argon = argon2
        )
        workerRoute(
            workerRepository = workerRepository,
            ordersRepository = ordersRepository
        )
        consumerRoute(
            consumerRepository = consumerRepository,
            ordersRepository = ordersRepository,
            workerRepository = workerRepository,
            addressesRepository = addressesRepository
        )
        utilsRoute()
    }
}