package ru.utilityorders.backend.entities

import kotlinx.serialization.Serializable

@Serializable
class SignUpRequest(
    val firstName: String,
    val lastName: String,
    val surname: String,
    val gender: String,
    val gettingStarted: String,
    val dateOfBirth: String
)