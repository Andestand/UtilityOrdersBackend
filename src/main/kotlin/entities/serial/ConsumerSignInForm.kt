package ru.utilityorders.backend.entities.serial

import kotlinx.serialization.Serializable

@Serializable
class ConsumerSignInForm(
    val email: String,
    val password: String
)