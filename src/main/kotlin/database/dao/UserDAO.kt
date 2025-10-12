package ru.utilityorders.backend.database.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class UserDAO(id: EntityID<UUID>): UUIDEntity(id) {

    companion object: UUIDEntityClass<UserDAO>(UserTable)

    var firstName by UserTable.firstName
    var lastName by UserTable.lastName
    var surname by UserTable.surname

    var email by UserTable.email
    var dateOfBirth by UserTable.dateOfBirth
    var password by UserTable.password

    var dateOfRegistration by UserTable.dateOfRegistration
}