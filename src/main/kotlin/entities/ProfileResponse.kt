package ru.utilityorders.backend.entities

import kotlinx.serialization.Serializable

@Serializable
class ProfileResponse(
    val id: String,
    val firstName: String,
    val lastName: String,
    val surname: String,
    val dateOfBirth: String,
    val dateOfRegistration: String
)