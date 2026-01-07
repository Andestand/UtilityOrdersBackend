package ru.utilityorders.backend.database.entities

import kotlinx.datetime.LocalDate

data class AddWorkerDB(
    val firstName: String,
    val lastName: String,
    val surname: String,
    val gender: String,
    val gettingStarted: LocalDate,
    val dateOfBirth: LocalDate
)