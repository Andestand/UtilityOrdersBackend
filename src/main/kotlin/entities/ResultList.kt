package ru.utilityorders.backend.entities

import kotlinx.serialization.Serializable

@Serializable
data class ResultList<T>(
    val result: List<T>,
    val limit: Int
)