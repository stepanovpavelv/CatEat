package com.example.cateat.service.registry

import kotlinx.serialization.Serializable

@Serializable
data class IndicationItemDto(val id: Int, val date: String, val value: Int)