package org.byte_bloom.flux.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WarehouseResponseDto(
    val id: String,
    val name: String,
    val regional_zone: String,
    val latitude: Double,
    val longitude: Double
)
