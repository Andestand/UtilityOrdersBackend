package ru.utilityorders.backend.entities.serial

import kotlinx.serialization.Serializable
import ru.utilityorders.backend.entities.serial.JwtResponse

@Serializable
data class WorkerSuccessAuthResponse(
    val token: String,
    val jwt: JwtResponse
)