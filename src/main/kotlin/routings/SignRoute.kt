package ru.utilityorders.backend.routings

import de.mkammerer.argon2.Argon2
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import ru.utilityorders.backend.database.repository.UserRepository
import ru.utilityorders.backend.resources.SignIn
import ru.utilityorders.backend.resources.SignUp

fun Route.signRoute(argon2: Argon2, repository: UserRepository) {
    post<SignIn> {

    }

    post<SignUp> {

    }
}