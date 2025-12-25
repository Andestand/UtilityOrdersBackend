package ru.utilityorders.backend.entities

import kotlinx.serialization.Serializable

@Serializable
data class JwtResponse(
    val accessToken: String,
    val refreshToken: String? = null
)