package ru.utilityorders.backend.entities

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrder(
    val header: String,
    val costOfWork: Long
)
