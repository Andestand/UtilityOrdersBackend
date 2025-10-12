package ru.utilityorders.backend.routings

import de.mkammerer.argon2.Argon2
import io.ktor.server.application.Application
import io.ktor.server.resources.get
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.utilityorders.backend.database.repository.UserRepository
import ru.utilityorders.backend.resources.RootRes

fun Application.mainRouting() {
    val argon2 by inject<Argon2>()
    val userRepository by inject<UserRepository>()

    routing {
        get<RootRes> {
            call.respondText("Hello, world!")
        }
        signRoute(argon2, userRepository)
    }
}