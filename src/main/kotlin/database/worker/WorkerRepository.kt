package ru.utilityorders.backend.database.worker

import de.mkammerer.argon2.Argon2
import org.jetbrains.exposed.sql.update
import ru.utilityorders.backend.database.entities.AddWorkerDB
import ru.utilityorders.backend.database.entities.CredentialsWorkerDB
import ru.utilityorders.backend.database.entities.WorkerDB
import ru.utilityorders.backend.utils.generateUserToken
import ru.utilityorders.backend.utils.passwordToHash
import ru.utilityorders.backend.utils.suspendTransaction
import ru.utilityorders.backend.utils.toDB
import java.time.OffsetDateTime
import java.util.UUID

interface WorkerRepository {

    suspend fun addUser(user: AddWorkerDB): String

    suspend fun findUserByToken(token: String): Pair<UUID, String>?

    suspend fun findUserByUID(uid: UUID): WorkerDB?

    suspend fun usersTokenList(): List<CredentialsWorkerDB>

    suspend fun workerList(): List<WorkerDB>

    suspend fun updateUserSecret(uid: UUID): Boolean
}

class WorkerRepositoryImpl(private val argon: Argon2): WorkerRepository {
    override suspend fun addUser(user: AddWorkerDB) =
        suspendTransaction {
            val newToken = generateUserToken()

            WorkerDAO.new {
                firstName = user.firstName
                lastName = user.lastName
                surname = user.surname

                gender = user.gender
                gettingStarted = user.gettingStarted

                dateOfBirth = user.dateOfBirth

                userToken = argon.passwordToHash(newToken)
                userSecret = generateUserToken()

                dateOfRegistration = OffsetDateTime.now()
            }
            newToken
        }

    override suspend fun findUserByToken(token: String) =
        suspendTransaction {
            WorkerDAO.find {
                WorkerTable.userToken eq token
            }.map { it.id.value to it.userToken }.firstOrNull()
        }

    override suspend fun findUserByUID(uid: UUID) =
        suspendTransaction {
            WorkerDAO.find {
                WorkerTable.id eq uid
            }.map { it.toDB() }.firstOrNull()
        }

    override suspend fun usersTokenList() =
        suspendTransaction {
            WorkerDAO.all()
                .map {
                    CredentialsWorkerDB(
                        id = it.id.value,
                        userToken = it.userToken,
                        userSecret = it.userSecret
                    )
                }
        }

    override suspend fun workerList() =
        suspendTransaction {
            WorkerDAO.all().map { it.toDB() }
        }

    override suspend fun updateUserSecret(uid: UUID) =
        suspendTransaction {
            WorkerTable.update({ WorkerTable.id eq uid }) {
                it[userSecret] = generateUserToken()
            } != 0
        }
}