package ru.utilityorders.backend.resources

import io.ktor.resources.Resource

@Resource("sign_up")
data class SignUp(val parent: ApiRes = ApiRes())
