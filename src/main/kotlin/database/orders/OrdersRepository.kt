package ru.utilityorders.backend.database.orders

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update
import ru.utilityorders.backend.database.entities.OrderDB
import ru.utilityorders.backend.utils.suspendTransaction
import ru.utilityorders.backend.utils.toDB
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

interface OrdersRepository {

    suspend fun addOrder(
        header: String,
        consumerID: UUID,
        workerID: UUID,
        costOfWork: BigDecimal,
        address: String,
        executionDate: LocalDate
    )

    suspend fun getOrdersByWorkerID(uid: UUID): List<OrderDB>

    suspend fun orderList(): List<OrderDB>

    suspend fun getOrdersByConsumerID(id: UUID): List<OrderDB>

    suspend fun findOrderByID(id: UUID): OrderDB?

    suspend fun atWork(uid: UUID, orderID: UUID, value: Boolean)

}

class OrdersRepositoryImpl: OrdersRepository {

    override suspend fun addOrder(
        header: String,
        consumerID: UUID,
        workerID: UUID,
        costOfWork: BigDecimal,
        address: String,
        executionDate: LocalDate
    ): Unit =
        suspendTransaction {
            OrdersDAO.new {
                this.header = header
                this.consumerID = consumerID
                this.workerID = workerID
                this.costOfWork = costOfWork
                this.address = address
                status = 0
                atWork = false
                dateOfAdded = OffsetDateTime.now()
                this.executionDate = executionDate
            }
        }

    override suspend fun getOrdersByWorkerID(uid: UUID) =
        suspendTransaction {
            OrdersDAO
                .find(OrdersTable.workerID eq uid)
                .sortedBy { it.dateOfAdded }
                .toDB()
        }

    override suspend fun orderList() =
        suspendTransaction {
            OrdersDAO.all().map { it.toDB() }
        }

    override suspend fun getOrdersByConsumerID(id: UUID) =
        suspendTransaction {
            OrdersDAO
                .find(OrdersTable.workerID eq id)
                .sortedBy { it.dateOfAdded }
                .toDB()
        }

    override suspend fun findOrderByID(id: UUID) =
        suspendTransaction {
            OrdersDAO
                .find(OrdersTable.id eq id)
                .firstOrNull()?.toDB()
        }

    override suspend fun atWork(uid: UUID, orderID: UUID, value: Boolean): Unit =
        suspendTransaction {
            OrdersTable.update({(OrdersTable.workerID eq uid) and (OrdersTable.id eq orderID)}) {
                it[atWork] = value
            }
        }
}