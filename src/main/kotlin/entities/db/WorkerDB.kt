package ru.utilityorders.backend.entities.db

import kotlinx.datetime.LocalDate
import java.time.OffsetDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class WorkerDB(
    val id: Uuid,
    val firstName: String,
    val lastName: String,
    val surname: String,
    val dateBirth: LocalDate,
    val limit: Int,
    val createdAt: OffsetDateTime
)