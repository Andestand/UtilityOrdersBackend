package ru.utilityorders.backend.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.utilityorders.backend.di.KoinModule

fun Application.koinPlugin() {
    install(Koin) {
        slf4jLogger()
        modules(KoinModule)
    }
}