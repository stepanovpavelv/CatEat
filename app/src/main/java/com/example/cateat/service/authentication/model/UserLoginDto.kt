package com.example.cateat.service.authentication.model

import kotlinx.serialization.Serializable

/**
 * Dto-объект  создания показателя.
 */
@Serializable
data class UserLoginDto(
    val login: String,
    val password: String
)