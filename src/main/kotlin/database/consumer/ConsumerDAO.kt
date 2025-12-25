package ru.utilityorders.backend.database.consumer

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class ConsumerDAO(id: EntityID<UUID>): UUIDEntity(id) {

    companion object: UUIDEntityClass<ConsumerDAO>(ConsumerTable)


}