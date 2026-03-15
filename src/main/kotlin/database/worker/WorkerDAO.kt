package ru.utilityorders.backend.database.worker

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class WorkerDAO(id: EntityID<Uuid>): UuidEntity(id) {

    companion object: UuidEntityClass<WorkerDAO>(WorkerTable)

    var firstName by WorkerTable.firstName
    var lastName by WorkerTable.lastName
    var surname by WorkerTable.surname

    var orderLimit by WorkerTable.orderLimit

    var gender by WorkerTable.gender
    var gettingStarted by WorkerTable.gettingStarted

    var userToken by WorkerTable.userToken

    var dateBirth by WorkerTable.dateBirth

    var createdAt by WorkerTable.createdAt
}