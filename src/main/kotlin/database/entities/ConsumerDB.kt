package ru.utilityorders.backend.database.entities

import java.time.OffsetDateTime
import java.util.UUID

data class ConsumerDB(
    val id: UUID,
    val firstName: String,
    val email: String,
    val address: String?,
    val dateOfRegistration: OffsetDateTime
)