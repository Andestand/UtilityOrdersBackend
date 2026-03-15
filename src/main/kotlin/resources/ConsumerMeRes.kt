package ru.utilityorders.backend.resources

import io.ktor.resources.Resource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Resource("me")
data class ConsumerMeRes(val parent: ApiRes = ApiRes()) {

    @Resource("create_order")
    data class CreateOrder(val parent: ConsumerMeRes = ConsumerMeRes())

    @Resource("addresses")
    data class Addresses(val parent: ConsumerMeRes = ConsumerMeRes()) {

        @OptIn(ExperimentalUuidApi::class)
        @Resource("{id}")
        data class Id(val parent: Addresses = Addresses(), val id: Uuid)
    }

    @Resource("address_default")
    data class AddressDefault(val parent: ConsumerMeRes = ConsumerMeRes())

    @Resource("add_address")
    data class AddAddress(val parent: ConsumerMeRes = ConsumerMeRes())
}