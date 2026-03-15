package ru.utilityorders.backend.entities.serial

import kotlinx.serialization.Serializable

@Serializable
data class AddAddress(
    val country: String,
    val region: String,
    val city: String,
    val street: String,
    val house: String,
    val apartment: String?
)