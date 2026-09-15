package org.byte_bloom.flux.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WarehouseRequestDto(
    val name: String,
    val regional_zone: String,
    val latitude: Double,
    val longitude: Double
)
