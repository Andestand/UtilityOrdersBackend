package ru.utilityorders.backend.database.orders

import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import ru.utilityorders.backend.database.consumer.ConsumerTable
import ru.utilityorders.backend.database.residential_addresses.ResidentialAddressesTable
import ru.utilityorders.backend.database.worker.WorkerTable
import java.time.OffsetDateTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object OrdersTable: UuidTable("orders") {
    val header = varchar("header", 250)
    val workerID = uuid("worker_id").references(WorkerTable.id)
    val consumerID = uuid("consumer_id").references(ConsumerTable.id)
    val costOfWork = decimal("cost_of_work", 20, 2)
    val address = uuid("address").references(ResidentialAddressesTable.id)
    val completedAt = timestampWithTimeZone("completed_at").nullable()
    val executionAt = date("execution_at")
    val createdAt = timestampWithTimeZone("created_at").default(OffsetDateTime.now())
}