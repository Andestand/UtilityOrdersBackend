package ru.utilityorders.backend.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.resources.Resources

fun Application.resourcesPlugin() {
    install(Resources)
}