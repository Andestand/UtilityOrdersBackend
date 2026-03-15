package ru.utilityorders.backend.utils

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.jdbc.SizedIterable
import ru.utilityorders.backend.database.consumer.ConsumerDAO
import ru.utilityorders.backend.database.orders.OrdersDAO
import ru.utilityorders.backend.database.worker.WorkerDAO
import ru.utilityorders.backend.entities.serial.ConsumerSignUpForm
import ru.utilityorders.backend.entities.Order
import ru.utilityorders.backend.entities.ProfileResponse
import ru.utilityorders.backend.entities.db.AddAddressDB
import ru.utilityorders.backend.entities.db.AddConsumerDB
import ru.utilityorders.backend.entities.db.AddWorkerDB
import ru.utilityorders.backend.entities.db.AddressDB
import ru.utilityorders.backend.entities.db.ConsumerDB
import ru.utilityorders.backend.entities.db.OrderDB
import ru.utilityorders.backend.entities.db.WorkerDB
import ru.utilityorders.backend.entities.serial.AddAddress
import ru.utilityorders.backend.entities.serial.Address
import ru.utilityorders.backend.entities.serial.WorkerSignUpForm
import kotlin.toString
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun WorkerSignUpForm.toDB() =
    AddWorkerDB(
        firstName = firstName,
        lastName = lastName,
        surname = surname,
        gender = gender,
        gettingStarted = LocalDate.parse(gettingStarted),
        dateBirth = LocalDate.parse(dateBirth)
    )

@OptIn(ExperimentalUuidApi::class)
fun OrdersDAO.toDB() =
    OrderDB(
        id = id.value,
        header = header,
        consumerName = consumerID.toString(),
        workerName = workerID.toString(),
        workerID = workerID,
        costOfWork = costOfWork,
        address = address.toString(),
        executionAt = executionAt,
        completedAt = completedAt,
        createdAt = createdAt
    )

fun ConsumerSignUpForm.toDB() = AddConsumerDB(firstName, email, password)

fun List<OrderDB>.toOrderSerial() = map { it.toOrderSerial() }

@OptIn(ExperimentalUuidApi::class)
fun OrderDB.toOrderSerial() =
    Order(
        id = id.toString(),
        header = header,
        consumerName = consumerName,
        workerName = workerName,
        costOfWork = costOfWork.toLong(),
        address = address,
        createdAt = createdAt.toString(),
        executionAt = executionAt.toString(),
        completedAt = completedAt?.toString()
    )

@OptIn(ExperimentalUuidApi::class)
fun ConsumerDAO.toDB() =
    ConsumerDB(
        id = id.value,
        firstName = firstName,
        email = email,
        address = addressDefault.toString(),
        dateOfRegistration = createdAt
    )

fun SizedIterable<WorkerDAO>.toDB() = map { it.toDB() }

@OptIn(ExperimentalUuidApi::class)
fun WorkerDAO.toDB() =
    WorkerDB(
        id = id.value,
        firstName = firstName,
        lastName = lastName,
        surname = surname,
        dateBirth = dateBirth,
        limit = orderLimit,
        createdAt = createdAt
    )

@OptIn(ExperimentalUuidApi::class)
fun WorkerDB.toOrderSerial() =
    ProfileResponse(id.toString(), firstName, lastName, surname, dateBirth.toString(), createdAt.toString())

@OptIn(ExperimentalUuidApi::class)
fun AddAddress.toDB(consumerID: Uuid) =
    AddAddressDB(consumerID, country, region, city, street, house, apartment)

@OptIn(ExperimentalUuidApi::class)
fun AddressDB.toAddressSerial() =
    Address(id.toString(), address, createAt.toString())

fun List<AddressDB>.toAddressSerial() = map { it.toAddressSerial() }