package org.byte_bloom.flux.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VehicleResponseDto(
    val id: String,
    val current_hub_id: String,
    val max_capacity_kg: Double,
    val cost_per_km: Double
)
