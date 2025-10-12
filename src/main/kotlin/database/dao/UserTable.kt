package ru.utilityorders.backend.database.dao

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object UserTable: UUIDTable("users") {

    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val surname = varchar("surname", 50)

    val email = text("email")
    val dateOfBirth = date("date_of_birth")
    val password = text("password")

    val dateOfRegistration = timestampWithTimeZone("date_of_registration")
}