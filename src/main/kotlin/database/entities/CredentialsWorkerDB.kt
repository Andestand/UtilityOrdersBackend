package ru.utilityorders.backend.database.entities

import java.util.UUID

class CredentialsWorkerDB(
    val id: UUID,
    val userToken: String,
    val userSecret: String
)