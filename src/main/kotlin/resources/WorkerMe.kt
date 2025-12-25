package ru.utilityorders.backend.resources

import io.ktor.resources.Resource

@Resource("me")
data class WorkerMe(val parent: ApiRes = ApiRes()) {

    @Resource("orders")
    data class Orders(val parent: WorkerMe = WorkerMe()) {

        @Resource("{id}")
        data class Order(val id: String, val parent: Orders = Orders()) {

            @Resource("proceed_to_order")
            data class ProceedToOrder(val parent: Order)
        }
    }

    @Resource("logout")
    data class Logout(val parent: WorkerMe = WorkerMe())
}