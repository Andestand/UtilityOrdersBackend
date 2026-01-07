package ru.utilityorders.backend.plugin

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import ru.utilityorders.backend.core.JWT_REALM
import ru.utilityorders.backend.core.JWT_SECRET
import ru.utilityorders.backend.entities.Message
import ru.utilityorders.backend.utils.AUTHORIZATION_REQUIRED
import java.util.UUID

fun Application.securityPlugin() {
    val secret = environment.config.property(JWT_SECRET).getString()
    val realm = environment.config.property(JWT_REALM).getString()

    authentication {
        jwt {
            verifier {
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .build()
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, Message(AUTHORIZATION_REQUIRED))
            }

            validate {
                try {
                    UUID.fromString(it.subject)
                } catch (_: IllegalArgumentException) { null }
            }

            this.realm = realm
        }
    }
}