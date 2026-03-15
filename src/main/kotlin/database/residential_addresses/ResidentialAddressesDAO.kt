package ru.utilityorders.backend.database.residential_addresses

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ResidentialAddressesDAO(id: EntityID<Uuid>): UuidEntity(id) {
    companion object: UuidEntityClass<ResidentialAddressesDAO>(ResidentialAddressesTable)

    var consumerID by ResidentialAddressesTable.consumerID
    var country by ResidentialAddressesTable.country
    var region by ResidentialAddressesTable.region
    var city by ResidentialAddressesTable.city
    var street by ResidentialAddressesTable.street
    var house by ResidentialAddressesTable.house
    var apartment by ResidentialAddressesTable.apartment
    var createAt by ResidentialAddressesTable.createAt
}