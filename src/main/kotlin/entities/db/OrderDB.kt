package ru.utilityorders.backend.entities.db

import kotlinx.datetime.LocalDate
import java.math.BigDecimal
import java.time.OffsetDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class OrderDB(
    val id: Uuid,
    val header: String,
    val workerName: String,
    val workerID: Uuid,
    val consumerName: String,
    val costOfWork: BigDecimal,
    val address: String,
    val executionAt: LocalDate,
    val completedAt: OffsetDateTime?,
    val createdAt: OffsetDateTime
)