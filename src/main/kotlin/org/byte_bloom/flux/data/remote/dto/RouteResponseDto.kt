package org.byte_bloom.flux.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RouteResponseDto(
    val id: String,
    val origin_hub_id: String,
    val destination_hub_id: String,
    val distance_km: Double,
    val typical_delay_min: Double
)
