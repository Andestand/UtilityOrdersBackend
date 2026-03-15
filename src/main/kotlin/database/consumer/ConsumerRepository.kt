package ru.utilityorders.backend.database.consumer

import de.mkammerer.argon2.Argon2
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.update
import ru.utilityorders.backend.entities.db.AddConsumerDB
import ru.utilityorders.backend.entities.db.ConsumerDB
import ru.utilityorders.backend.entities.CredentialsConsumerDB
import ru.utilityorders.backend.utils.passwordToHash
import ru.utilityorders.backend.utils.suspendTransaction
import ru.utilityorders.backend.utils.toDB
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface ConsumerRepository {

    suspend fun addUser(user: AddConsumerDB): ConsumerDAO

    suspend fun findUserByID(id: Uuid): ConsumerDB?

    suspend fun isCheckingUserByEmail(email: String): Boolean

    suspend fun findCredentialsByEmail(email: String): CredentialsConsumerDB?

    suspend fun setAddressDefault(uid: Uuid, addressID: Uuid)

    suspend fun getAddressDefault(uid: Uuid): Uuid?
}

@OptIn(ExperimentalUuidApi::class)
class ConsumerRepositoryImpl(private val argon: Argon2): ConsumerRepository {

    override suspend fun addUser(user: AddConsumerDB) =
        suspendTransaction {
            ConsumerDAO.new {
                firstName = user.firstName
                email = user.email
                password = argon.passwordToHash(user.password)
            }
        }

    override suspend fun findUserByID(id: Uuid) =
        suspendTransaction {
            ConsumerDAO.findById(id)?.toDB()
        }

    override suspend fun isCheckingUserByEmail(email: String): Boolean =
        suspendTransaction {
            ConsumerDAO
                .find { ConsumerTable.email eq email }
                .limit(1)
                .map { it.email }
                .firstOrNull() != null
        }

    override suspend fun findCredentialsByEmail(email: String): CredentialsConsumerDB? =
        suspendTransaction {
            ConsumerDAO
                .find { ConsumerTable.email eq email }
                .limit(1)
                .map {
                    CredentialsConsumerDB(
                        id = it.id.value,
                        email = it.email,
                        password = it.password
                    )
                }
                .firstOrNull()
        }

    override suspend fun setAddressDefault(uid: Uuid, addressID: Uuid): Unit =
        suspendTransaction {
            ConsumerTable.update({ ConsumerTable.id eq uid }) {
                it[addressDefault] = addressID
            }
        }

    override suspend fun getAddressDefault(uid: Uuid) =
        suspendTransaction {
            ConsumerDAO.findById(uid)?.addressDefault
        }
}