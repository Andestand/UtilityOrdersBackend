package ru.utilityorders.backend.database.worker

import de.mkammerer.argon2.Argon2
import org.jetbrains.exposed.v1.core.eq
import ru.utilityorders.backend.entities.db.AddWorkerDB
import ru.utilityorders.backend.entities.db.CredentialsWorkerDB
import ru.utilityorders.backend.entities.db.WorkerDB
import ru.utilityorders.backend.utils.generateUserToken
import ru.utilityorders.backend.utils.passwordToHash
import ru.utilityorders.backend.utils.suspendTransaction
import ru.utilityorders.backend.utils.toDB
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface WorkerRepository {

    suspend fun addUser(user: AddWorkerDB): Pair<Uuid, String>

    suspend fun findUserByToken(token: String): Pair<Uuid, String>?

    suspend fun findUserByUID(uid: Uuid): WorkerDB?

    suspend fun usersTokenList(): List<Pair<Uuid, String>>

    suspend fun workerList(): List<WorkerDB>
}

@OptIn(ExperimentalUuidApi::class)
class WorkerRepositoryImpl(private val argon: Argon2): WorkerRepository {
    override suspend fun addUser(user: AddWorkerDB) =
        suspendTransaction {
            val newToken = generateUserToken()

            val id = WorkerDAO.new {
                firstName = user.firstName
                lastName = user.lastName
                surname = user.surname

                gender = user.gender
                gettingStarted = user.gettingStarted

                dateBirth = user.dateBirth

                userToken = argon.passwordToHash(newToken)
            }.id.value
            id to newToken
        }

    override suspend fun findUserByToken(token: String) =
        suspendTransaction {
            WorkerDAO.find {
                WorkerTable.userToken eq token
            }.map { it.id.value to it.userToken }.firstOrNull()
        }

    override suspend fun findUserByUID(uid: Uuid) =
        suspendTransaction {
            WorkerDAO.findById(uid)?.toDB()
        }

    override suspend fun usersTokenList() =
        suspendTransaction {
            WorkerDAO.all()
                .map { it.id.value to it.userToken }
        }

    override suspend fun workerList() =
        suspendTransaction {
            WorkerDAO.all().toDB()
        }
}