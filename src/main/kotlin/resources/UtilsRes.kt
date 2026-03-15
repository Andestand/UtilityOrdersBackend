package ru.utilityorders.backend.resources

import io.ktor.resources.Resource

@Resource("utils")
data class UtilsRes(val parent: ApiRes = ApiRes()) {
    @Resource("new_jwt")
    data class NewJwt(val parent: UtilsRes = UtilsRes())
}