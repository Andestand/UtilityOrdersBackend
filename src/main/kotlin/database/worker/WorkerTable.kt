package ru.utilityorders.backend.database.worker

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object WorkerTable: UUIDTable("workers") {

    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val surname = varchar("surname", 50)

    val orderLimit = integer("order_limit").default(10)

    val gender = varchar("gender", 50)
    val gettingStarted = date("getting_started")

    val userToken = text("user_token").uniqueIndex()
    val userSecret = text("user_secret").uniqueIndex()

    val dateOfBirth = date("date_of_birth")

    val dateOfRegistration = timestampWithTimeZone("date_of_registration")
}