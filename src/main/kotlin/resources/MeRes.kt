package ru.utilityorders.backend.resources

import io.ktor.resources.Resource

@Resource("me")
data class MeRes(val parent: ApiRes = ApiRes()) {

    @Resource("orders")
    data class Orders(val parent: MeRes = MeRes()) {

        @Resource("{id}")
        data class Order(val id: String, val parent: Orders = Orders()) {

            @Resource("cancel_order")
            data class CancelOrder(val parent: Order)

            @Resource("proceed_to_order")
            data class ProceedToOrder(val parent: Order)

            @Resource("order_completed")
            data class OrderCompleted(val parent: Order)
        }
    }

    @Resource("create_order")
    data class CreateOrder(val parent: MeRes = MeRes())

    @Resource("logout")
    data class Logout(val parent: MeRes = MeRes())
}