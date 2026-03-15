package ru.utilityorders.backend.entities.serial

import kotlinx.serialization.Serializable

@Serializable
data class ConsumerSignUpForm(
    val firstName: String,
    val email: String,
    val password: String
)