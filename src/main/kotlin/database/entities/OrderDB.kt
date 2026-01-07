package ru.utilityorders.backend.database.entities

import kotlinx.datetime.LocalDate
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class OrderDB(
    val id: UUID,
    val header: String,
    val workerID: UUID,
    val consumerID: UUID,
    val costOfWork: BigDecimal,
    val address: String,
    val atWork: Boolean,
    val dateOfAdded: OffsetDateTime,
    val executionDate: LocalDate
)