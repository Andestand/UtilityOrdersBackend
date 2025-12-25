package ru.utilityorders.backend.di

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.utilityorders.backend.database.consumer.ConsumerRepository
import ru.utilityorders.backend.database.consumer.ConsumerRepositoryImpl
import ru.utilityorders.backend.database.orders.OrdersRepository
import ru.utilityorders.backend.database.orders.OrdersRepositoryImpl
import ru.utilityorders.backend.database.worker.WorkerRepository
import ru.utilityorders.backend.database.worker.WorkerRepositoryImpl

val KoinModule = module {
    single<Argon2> {
        Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64)
    }
    singleOf(::WorkerRepositoryImpl) bind WorkerRepository::class
    singleOf(::OrdersRepositoryImpl) bind OrdersRepository::class
    singleOf(::ConsumerRepositoryImpl) bind ConsumerRepository::class
}