package ru.utilityorders.backend.database.entities

import java.util.UUID

class CredentialsUserDB(
    val id: UUID,
    val userToken: String,
    val userSecret: String
)