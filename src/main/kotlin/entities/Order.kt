package ru.utilityorders.backend.entities

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val header: String,
    val workerID: String,
    val consumerID: String,
    val costOfWork: Long,
    val address: String,
    val atWork: Boolean,
    val dateOfAdded: String
)