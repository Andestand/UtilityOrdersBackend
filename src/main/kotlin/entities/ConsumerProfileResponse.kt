package ru.utilityorders.backend.entities

import kotlinx.serialization.Serializable

@Serializable
data class ConsumerProfileResponse(
    val id: String,
    val firstName: String,
    val email: String,
    val dateOfRegistration: String
)
