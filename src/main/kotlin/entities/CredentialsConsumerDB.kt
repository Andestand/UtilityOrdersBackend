package ru.utilityorders.backend.entities

import java.util.UUID

class CredentialsConsumerDB(
    val id: UUID,
    val email: String,
    val password: String,
    val userSecret: String
)