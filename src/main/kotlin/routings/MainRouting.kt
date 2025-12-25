package ru.utilityorders.backend.routings

import de.mkammerer.argon2.Argon2
import io.ktor.server.application.Application
import io.ktor.server.resources.get
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.utilityorders.backend.database.consumer.ConsumerRepository
import ru.utilityorders.backend.database.orders.OrdersRepository
import ru.utilityorders.backend.database.worker.WorkerRepository
import ru.utilityorders.backend.resources.RootRes

fun Application.mainRouting() {
    val argon2 by inject<Argon2>()
    val workerRepository by inject<WorkerRepository>()
    val ordersRepository by inject<OrdersRepository>()
    val consumerRepository by inject<ConsumerRepository>()

    routing {
        get<RootRes> {
            call.respondText("Hello World!")
        }

        workerRoute(
            argon2 = argon2,
            workerRepository = workerRepository,
            ordersRepository = ordersRepository
        )
        consumerRoute(
            argon2 = argon2,
            repository = consumerRepository
        )
    }
}