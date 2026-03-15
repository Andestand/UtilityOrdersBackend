package ru.utilityorders.backend.entities.db

import java.time.OffsetDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class ConsumerDB(
    val id: Uuid,
    val firstName: String,
    val email: String,
    val address: String?,
    val dateOfRegistration: OffsetDateTime
)