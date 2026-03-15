package ru.utilityorders.backend.database.residential_addresses

import org.jetbrains.exposed.v1.core.eq
import ru.utilityorders.backend.entities.db.AddAddressDB
import ru.utilityorders.backend.entities.db.AddressDB
import ru.utilityorders.backend.utils.suspendTransaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface ResidentialAddressesRepository {
    suspend fun addAddress(value: AddAddressDB)

    suspend fun fetchAddressesByUID(id: Uuid): List<AddressDB>
}

@OptIn(ExperimentalUuidApi::class)
class ResidentialAddressesRepositoryImpl: ResidentialAddressesRepository {
    override suspend fun addAddress(value: AddAddressDB): Unit =
        suspendTransaction {
            ResidentialAddressesDAO.new {
                consumerID = value.consumerID
                country = value.country
                region = value.region
                city = value.city
                street = value.street
                house = value.house
                apartment = value.apartment
            }
        }

    override suspend fun fetchAddressesByUID(id: Uuid): List<AddressDB> =
        suspendTransaction {
            ResidentialAddressesDAO
                .find { ResidentialAddressesTable.consumerID eq id }
                .map {
                    val address = if (it.apartment != null)
                        "${it.city}, ${it.street}, ${it.house}, ${it.apartment}"
                    else
                        "${it.city}, ${it.street}, ${it.house}"

                    AddressDB(
                        id = it.id.value,
                        address = address,
                        createAt = it.createAt
                    )
                }
        }
}