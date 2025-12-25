package ru.utilityorders.backend.resources

import io.ktor.resources.Resource

@Resource("worker")
data class Worker(val parent: ApiRes = ApiRes()) {

    @Resource("sign_in")
    data class SignIn(val parent: Worker = Worker())

    @Resource("sign_up")
    data class SignUp(val parent: Worker = Worker())
}