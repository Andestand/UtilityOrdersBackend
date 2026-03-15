package ru.utilityorders.backend.entities.db

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CredentialsWorkerDB(
    val id: Uuid,
    val userToken: String
)