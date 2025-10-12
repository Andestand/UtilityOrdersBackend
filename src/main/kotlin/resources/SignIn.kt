package ru.utilityorders.backend.resources

import io.ktor.resources.Resource

@Resource("sign_in")
data class SignIn(val parent: ApiRes = ApiRes())