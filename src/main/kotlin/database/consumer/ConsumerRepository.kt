package ru.utilityorders.backend.database.consumer

import de.mkammerer.argon2.Argon2
import ru.utilityorders.backend.database.entities.AddConsumerDB
import ru.utilityorders.backend.database.entities.ConsumerDB
import ru.utilityorders.backend.entities.CredentialsConsumerDB
import ru.utilityorders.backend.utils.generateUserToken
import ru.utilityorders.backend.utils.passwordToHash
import ru.utilityorders.backend.utils.suspendTransaction
import ru.utilityorders.backend.utils.toDB
import java.time.OffsetDateTime
import java.util.UUID

interface ConsumerRepository {

    suspend fun addUser(user: AddConsumerDB)

    suspend fun findUserByID(id: UUID): ConsumerDB?

    suspend fun usersCredentialsList(): List<CredentialsConsumerDB>

}

class ConsumerRepositoryImpl(private val argon: Argon2): ConsumerRepository {

    override suspend fun addUser(user: AddConsumerDB): Unit =
        suspendTransaction {
            ConsumerDAO.new {
                firstName = user.firstName
                email = user.email
                password = argon.passwordToHash(user.password)
                dateOfRegistration = OffsetDateTime.now()
                userSecret = generateUserToken()
            }
        }

    override suspend fun findUserByID(id: UUID) =
        suspendTransaction {
            ConsumerDAO.find {
                ConsumerTable.id eq id
            }.map { it.toDB() }.firstOrNull()
        }

    override suspend fun usersCredentialsList() =
        suspendTransaction {
            ConsumerDAO.all()
                .map {
                    CredentialsConsumerDB(
                        id = it.id.value,
                        email = it.email,
                        password = it.password,
                        userSecret = it.userSecret
                    )
                }
        }
}