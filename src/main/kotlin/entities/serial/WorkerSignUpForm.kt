package ru.utilityorders.backend.entities.serial

import kotlinx.serialization.Serializable

@Serializable
data class WorkerSignUpForm(
    val firstName: String,
    val lastName: String,
    val surname: String,
    val gender: String,
    val gettingStarted: String,
    val dateBirth: String
)