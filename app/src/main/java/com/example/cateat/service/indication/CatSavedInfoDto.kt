package com.example.cateat.service.indication

import kotlinx.serialization.Serializable

/**
 * Dto-объект создания показателя.
 */
@Serializable
data class CatSavedInfoDto(val date: String, val value: Int)