package ru.utilityorders.backend.di

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.utilityorders.backend.database.repository.UserRepository
import ru.utilityorders.backend.database.repository.UserRepositoryImpl

val KoinModule = module {
    single<Argon2> {
        Argon2Factory.create()
    }
    singleOf(::UserRepositoryImpl) bind UserRepository::class
}