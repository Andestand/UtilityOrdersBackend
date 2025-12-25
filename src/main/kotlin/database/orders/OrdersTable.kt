package ru.utilityorders.backend.database.orders

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object OrdersTable: UUIDTable("orders") {
    val header = varchar("header", 250)
    val workerID = uuid("worker_id")
    val consumerID = uuid("consumer_id")
    val costOfWork = decimal("cost_of_work", 20, 2)
    val address = text("address")
    val status = integer("status")
    val atWork = bool("at_work")
    val dateOfAdded = timestampWithTimeZone("date_of_added")
}