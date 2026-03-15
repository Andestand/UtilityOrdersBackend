package ru.utilityorders.backend.database.worker

import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import java.time.OffsetDateTime

object WorkerTable: UuidTable("workers") {

    val firstName = text("first_name")
    val lastName = text("last_name")
    val surname = text("surname")

    val orderLimit = integer("order_limit").default(10)

    val gender = text("gender")
    val gettingStarted = date("getting_started")

    val userToken = text("user_token").uniqueIndex()

    val dateBirth = date("date_birth")

    val createdAt = timestampWithTimeZone("created_at").default(OffsetDateTime.now())
}