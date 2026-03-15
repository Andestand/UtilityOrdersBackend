package ru.utilityorders.backend.entities

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val header: String,
    val workerName: String,
    val consumerName: String,
    val costOfWork: Long,
    val address: String,
    val createdAt: String,
    val executionAt: String,
    val completedAt: String?
)