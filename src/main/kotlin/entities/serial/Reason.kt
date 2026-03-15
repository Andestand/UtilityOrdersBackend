package ru.utilityorders.backend.entities.serial

import kotlinx.serialization.Serializable

@Serializable
data class Reason(
    val field: String,
    val message: String
)