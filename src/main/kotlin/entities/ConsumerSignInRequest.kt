package ru.utilityorders.backend.entities

import kotlinx.serialization.Serializable

@Serializable
class ConsumerSignInRequest(
    val email: String,
    val password: String
)