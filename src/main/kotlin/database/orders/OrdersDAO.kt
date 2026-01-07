package ru.utilityorders.backend.database.orders

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class OrdersDAO(id: EntityID<UUID>): UUIDEntity(id) {

    companion object: UUIDEntityClass<OrdersDAO>(OrdersTable)

    var header by OrdersTable.header
    var workerID by OrdersTable.workerID
    var consumerID by OrdersTable.consumerID
    var costOfWork by OrdersTable.costOfWork
    var address by OrdersTable.address
    var status by OrdersTable.status
    var atWork by OrdersTable.atWork
    var dateOfAdded by OrdersTable.dateOfAdded
    var executionDate by OrdersTable.executionDate
}