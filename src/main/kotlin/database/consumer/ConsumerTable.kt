package ru.utilityorders.backend.database.consumer

import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import ru.utilityorders.backend.database.residential_addresses.ResidentialAddressesTable
import java.time.OffsetDateTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object ConsumerTable: UuidTable("consumers") {

    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50).nullable()
    val surname = varchar("surname", 50).nullable()
    val addressDefault = uuid("address_default")
        .references(ResidentialAddressesTable.id)
        .nullable()
    val email = text("email").uniqueIndex()
    val password = text("password")
    val createdAt = timestampWithTimeZone("created_at").default(OffsetDateTime.now())
}