package ru.utilityorders.backend.database.orders

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update
import ru.utilityorders.backend.database.entities.OrderDB
import ru.utilityorders.backend.utils.suspendTransaction
import ru.utilityorders.backend.utils.toDB
import java.util.UUID

interface OrdersRepository {

    suspend fun getOrdersByUser(uid: UUID): List<OrderDB>

    suspend fun atWork(uid: UUID, orderID: UUID, value: Boolean)

}

class OrdersRepositoryImpl: OrdersRepository {

    override suspend fun getOrdersByUser(uid: UUID): List<OrderDB> =
        suspendTransaction {
            OrdersDAO
                .find(OrdersTable.workerID eq uid)
                .sortedBy { it.dateOfAdded }
                .toDB()
        }

    override suspend fun atWork(uid: UUID, orderID: UUID, value: Boolean): Unit =
        suspendTransaction {
            OrdersTable.update({(OrdersTable.workerID eq uid) and (OrdersTable.id eq orderID)}) {
                it[atWork] = value
            }
        }
}