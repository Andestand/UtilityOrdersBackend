package ru.utilityorders.backend.entities.db

import java.time.OffsetDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class AddressDB(
    val id: Uuid,
    val address: String,
    val createAt: OffsetDateTime
)