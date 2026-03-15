package ru.utilityorders.backend.database.residential_addresses

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import ru.utilityorders.backend.database.consumer.ConsumerTable
import java.time.OffsetDateTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object ResidentialAddressesTable: UuidTable("residential_addresses") {

    val consumerID = uuid("consumer_id")
        .references(ConsumerTable.id, ReferenceOption.CASCADE)

    val country = text("country")
    val region = text("region")
    val city = text("city")
    val street = text("street")
    val house = text("house")
    val apartment = text("apartment").nullable()
    val createAt = timestampWithTimeZone("create_at").default(OffsetDateTime.now())
}