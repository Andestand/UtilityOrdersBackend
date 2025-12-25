package ru.utilityorders.backend.database.worker

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.utilityorders.backend.database.worker.WorkerTable
import java.util.UUID

class WorkerDAO(id: EntityID<UUID>): UUIDEntity(id) {

    companion object: UUIDEntityClass<WorkerDAO>(WorkerTable)

    var firstName by WorkerTable.firstName
    var lastName by WorkerTable.lastName
    var surname by WorkerTable.surname

    var orderLimit by WorkerTable.orderLimit

    var gender by WorkerTable.gender
    var gettingStarted by WorkerTable.gettingStarted

    var userToken by WorkerTable.userToken
    var userSecret by WorkerTable.userSecret

    var dateOfBirth by WorkerTable.dateOfBirth

    var dateOfRegistration by WorkerTable.dateOfRegistration
}