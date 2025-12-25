package ru.utilityorders.backend.routings

import de.mkammerer.argon2.Argon2
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import ru.utilityorders.backend.core.JWT_SECRET
import ru.utilityorders.backend.database.consumer.ConsumerRepository

fun Route.consumerRoute(argon2: Argon2, repository: ConsumerRepository) {
    val jwtSecret = environment.config.property(JWT_SECRET).getString()

    authenticate {

    }
}