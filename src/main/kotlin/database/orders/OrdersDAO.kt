package ru.utilityorders.backend.database.orders

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class OrdersDAO(id: EntityID<Uuid>): UuidEntity(id) {

    companion object: UuidEntityClass<OrdersDAO>(OrdersTable)

    var header by OrdersTable.header
    var workerID by OrdersTable.workerID
    var consumerID by OrdersTable.consumerID
    var costOfWork by OrdersTable.costOfWork
    var address by OrdersTable.address
    var createdAt by OrdersTable.createdAt
    var executionAt by OrdersTable.executionAt
    var completedAt by OrdersTable.completedAt
}