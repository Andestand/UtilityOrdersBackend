package ru.utilityorders.backend.plugin

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import ru.utilityorders.backend.utils.AUTHORIZATION_REQUIRED
import ru.utilityorders.backend.utils.JWT_REALM
import ru.utilityorders.backend.utils.JWT_SECRET
import ru.utilityorders.backend.utils.toUuidOrNull
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Application.securityPlugin() {
    val jwtSecret = environment.config.property(JWT_SECRET).getString()
    val jwtRealm = environment.config.property(JWT_REALM).getString()

    authentication {
        jwt("access") {
            verifier {
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withClaim("type", "access")
                    .build()
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, AUTHORIZATION_REQUIRED)
            }

            validate { it.subject.toUuidOrNull() }

            realm = jwtRealm
        }

        jwt("refresh") {
            verifier {
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withClaim("type", "refresh")
                    .build()
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, AUTHORIZATION_REQUIRED)
            }

            validate { it.subject.toUuidOrNull() }

            realm = jwtRealm
        }
    }
}