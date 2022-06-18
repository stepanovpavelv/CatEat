package com.example.cateat

import kotlinx.serialization.Serializable

/**
 * Dto-объект создания показателя.
 */
@Serializable
data class CatSavedInfoDto(val date: String, val value: Int)