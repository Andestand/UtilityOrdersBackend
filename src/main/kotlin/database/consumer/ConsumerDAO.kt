package ru.utilityorders.backend.database.consumer

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ConsumerDAO(id: EntityID<Uuid>): UuidEntity(id) {

    companion object: UuidEntityClass<ConsumerDAO>(ConsumerTable)

    var firstName by ConsumerTable.firstName
    var email by ConsumerTable.email
    var password by ConsumerTable.password
    var addressDefault by ConsumerTable.addressDefault
    var createdAt by ConsumerTable.createdAt
}