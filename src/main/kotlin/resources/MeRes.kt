package ru.utilityorders.backend.resources

import io.ktor.resources.Resource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Resource("me")
data class MeRes(val parent: ApiRes = ApiRes()) {

    @Resource("orders")
    data class Orders(val parent: MeRes = MeRes()) {

        @OptIn(ExperimentalUuidApi::class)
        @Resource("{id}")
        data class Order(val id: Uuid, val parent: Orders = Orders()) {
            @Resource("completed")
            data class Completed(val parent: Order)
        }
    }

    @Resource("create_order")
    data class CreateOrder(val parent: MeRes = MeRes())

    @Resource("logout")
    data class Logout(val parent: MeRes = MeRes())
}