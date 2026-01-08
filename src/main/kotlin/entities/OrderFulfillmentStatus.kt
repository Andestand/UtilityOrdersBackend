package ru.utilityorders.backend.entities

enum class OrderFulfillmentStatus(val status: Int) {
    CANCEL(-1),
    NOT_STARTED(0),
    IN_PROGRESS(1),
    COMPLETED(2)
}