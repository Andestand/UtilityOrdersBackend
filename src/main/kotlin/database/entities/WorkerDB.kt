package ru.utilityorders.backend.database.entities

import kotlinx.datetime.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class WorkerDB(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val surname: String,
    val dateOfBirth: LocalDate,
    val limit: Int,
    val dateOfRegistration: OffsetDateTime
)