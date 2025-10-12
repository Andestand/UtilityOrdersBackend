package ru.utilityorders.backend

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.utilityorders.backend.core.HOST
import ru.utilityorders.backend.core.PORT
import ru.utilityorders.backend.plugin.*
import ru.utilityorders.backend.routings.mainRouting

fun main() {
    embeddedServer(
        factory = Netty,
        host = HOST,
        port = PORT,
        module = Application::plugins
    ).start(true)
}

private fun Application.plugins() {
    serializationPlugin()
    resourcesPlugin()
    koinPlugin()
    securityPlugin()
    mainRouting()
}