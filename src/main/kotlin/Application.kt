package ru.utilityorders.backend

import io.ktor.server.application.*
import io.ktor.server.netty.*
import ru.utilityorders.backend.plugin.*
import ru.utilityorders.backend.routings.mainRouting

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.plugins() {
    serializationPlugin()
    resourcesPlugin()
    koinPlugin()
    securityPlugin()
    mainRouting()
}