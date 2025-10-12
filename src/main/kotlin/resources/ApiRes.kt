package ru.utilityorders.backend.resources

import io.ktor.resources.Resource

@Resource("api")
data class ApiRes(val parent: RootRes = RootRes)