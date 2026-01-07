package ru.utilityorders.backend.entities

import kotlinx.serialization.Serializable

@Serializable
class ConsumerSignUpRequest(
    val firstName: String,
    val email: String,
    val password: String
)