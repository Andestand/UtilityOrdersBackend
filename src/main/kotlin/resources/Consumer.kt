package ru.utilityorders.backend.resources

import io.ktor.resources.Resource

@Resource("consumer")
data class Consumer(val parent: ApiRes = ApiRes()) {

    @Resource("sign_up")
    data class SignUp(val parent: Consumer = Consumer())

    @Resource("sign_in")
    data class SignIn(val parent: Consumer = Consumer())
}