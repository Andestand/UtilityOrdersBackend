package ru.utilityorders.backend.routings.api

import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import ru.utilityorders.backend.entities.serial.JwtResponse
import ru.utilityorders.backend.resources.UtilsRes
import ru.utilityorders.backend.utils.JWT_SECRET
import ru.utilityorders.backend.utils.createAccessJWT
import ru.utilityorders.backend.utils.createRefreshJWT
import ru.utilityorders.backend.utils.getWrapperCheckValidId
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Route.utilsRoute() {
    val jwtSecret = environment.config.property(JWT_SECRET).getString()

    authenticate("refresh") {
        getWrapperCheckValidId<UtilsRes.NewJwt> { _, uid ->
            call.respond(
                JwtResponse(
                    createAccessJWT(uid.toString(), jwtSecret),
                    createRefreshJWT(uid.toString(), jwtSecret)
                )
            )
        }
    }
}