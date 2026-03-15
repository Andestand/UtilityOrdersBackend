package ru.utilityorders.backend.database.orders

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import ru.utilityorders.backend.database.consumer.ConsumerRepository
import ru.utilityorders.backend.database.consumer.ConsumerTable
import ru.utilityorders.backend.database.residential_addresses.ResidentialAddressesTable
import ru.utilityorders.backend.database.worker.WorkerTable
import ru.utilityorders.backend.entities.db.OrderDB
import ru.utilityorders.backend.utils.concatWS
import ru.utilityorders.backend.utils.suspendTransaction
import ru.utilityorders.backend.utils.toDB
import java.math.BigDecimal
import java.time.OffsetDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface OrdersRepository {

    suspend fun addOrder(
        header: String,
        consumerID: Uuid,
        workerID: Uuid,
        costOfWork: BigDecimal,
        executionAt: LocalDate
    )

    suspend fun getOrdersByUID(uid: Uuid): List<OrderDB>

    suspend fun orderList(): List<OrderDB>

    suspend fun findOrderByIdAndWorkerId(uid: Uuid, orderID: Uuid): OrderDB?

    suspend fun findOrderByIdAndConsumerId(uid: Uuid, orderID: Uuid): OrderDB?

    suspend fun orderCompletedAt(uid: Uuid, orderID: Uuid)
}

@OptIn(ExperimentalUuidApi::class)
class OrdersRepositoryImpl(private val consumerRepository: ConsumerRepository): OrdersRepository {

    override suspend fun addOrder(
        header: String,
        consumerID: Uuid,
        workerID: Uuid,
        costOfWork: BigDecimal,
        executionAt: LocalDate
    ): Unit =
        suspendTransaction {
            val addressDefault = consumerRepository.getAddressDefault(consumerID)

            OrdersDAO.new {
                this.header = header
                this.consumerID = consumerID
                this.workerID = workerID
                this.costOfWork = costOfWork
                addressDefault?.let { this.address = it }
                this.executionAt = executionAt
            }
        }

    override suspend fun getOrdersByUID(uid: Uuid) =
        suspendTransaction {

            val workerName = concatWS(
                WorkerTable.lastName,
                WorkerTable.firstName,
                WorkerTable.surname
            )

            val consumerName = concatWS(
                ConsumerTable.lastName,
                ConsumerTable.firstName,
                ConsumerTable.surname
            )

            val address = concatWS(
                ", ",
                ResidentialAddressesTable.city,
                ResidentialAddressesTable.street,
                ResidentialAddressesTable.house,
                ResidentialAddressesTable.apartment
            )

            OrdersTable
                .join(WorkerTable, JoinType.INNER) {
                    OrdersTable.workerID eq WorkerTable.id
                }
                .join(ConsumerTable, JoinType.INNER) {
                    OrdersTable.consumerID eq ConsumerTable.id
                }
                .join(ResidentialAddressesTable, JoinType.INNER) {
                    OrdersTable.address eq ResidentialAddressesTable.id
                }
                .select(
                    OrdersTable.id,
                    OrdersTable.header,
                    workerName,
                    OrdersTable.workerID,
                    consumerName,
                    OrdersTable.costOfWork,
                    address,
                    OrdersTable.completedAt,
                    OrdersTable.executionAt,
                    OrdersTable.createdAt
                )
                .where {
                    (OrdersTable.workerID eq uid) or (OrdersTable.consumerID eq uid)
                }
                .map {
                    OrderDB(
                        id = it[OrdersTable.id].value,
                        header = it[OrdersTable.header],
                        workerName = it[workerName],
                        workerID = it[OrdersTable.workerID],
                        consumerName = it[consumerName],
                        costOfWork = it[OrdersTable.costOfWork],
                        address = it[address],
                        executionAt = it[OrdersTable.executionAt],
                        completedAt = it[OrdersTable.completedAt],
                        createdAt = it[OrdersTable.createdAt]
                    )
                }
        }

    override suspend fun orderList() =
        suspendTransaction {
            OrdersDAO.all().map { it.toDB() }
        }

    override suspend fun findOrderByIdAndWorkerId(uid: Uuid, orderID: Uuid) =
        suspendTransaction {
            OrdersDAO
                .find((OrdersTable.workerID eq uid) and (OrdersTable.id eq orderID))
                .firstOrNull()?.toDB()
        }

    override suspend fun findOrderByIdAndConsumerId(uid: Uuid, orderID: Uuid) =
        suspendTransaction {
            OrdersDAO
                .find((OrdersTable.consumerID eq uid) and (OrdersTable.id eq orderID))
                .firstOrNull()?.toDB()
        }

    override suspend fun orderCompletedAt(uid: Uuid, orderID: Uuid): Unit =
        suspendTransaction {
            OrdersTable.update({ (OrdersTable.workerID eq uid) and (OrdersTable.id eq orderID) }) {
                it[completedAt] = OffsetDateTime.now()
            }
        }
}