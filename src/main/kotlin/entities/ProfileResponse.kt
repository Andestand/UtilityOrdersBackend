package ru.utilityorders.backend.entities

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: String,
    val firstName: String,
    val lastName: String,
    val surname: String,
    val dateBirth: String,
    val createdAt: String
)