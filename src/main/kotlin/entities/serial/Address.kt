package ru.utilityorders.backend.entities.serial

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val id: String,
    val address: String,
    val createdAt: String
)