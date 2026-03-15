package ru.utilityorders.backend.entities.db

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class AddAddressDB(
    val consumerID: Uuid,
    val country: String,
    val region: String,
    val city: String,
    val street: String,
    val house: String,
    val apartment: String?
)