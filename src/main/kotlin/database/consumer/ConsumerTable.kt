package ru.utilityorders.backend.database.consumer

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object ConsumerTable: UUIDTable("consumers") {

    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50).nullable()
    val surname = varchar("surname", 50).nullable()

    val address = text("address")

    val email = text("email").uniqueIndex()
    val password = text("password").uniqueIndex()

    val dateOfRegistration = timestampWithTimeZone("date_of_registration")
}