package com.example.cateat.service.registry

import kotlinx.serialization.Serializable

@Serializable
data class RegistryRequestDto(val startDate: String, val finishDate: String)