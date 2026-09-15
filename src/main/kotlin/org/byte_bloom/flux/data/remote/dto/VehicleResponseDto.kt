package org.byte_bloom.flux.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class VehicleResponseDto(
    val id: String,
    @SerialName("current_hub_id")
    val currentHubId: String,
    @SerialName("max_capacity_kg")
    val maxCapacityKg: Double,
    @SerialName("cost_per_km")
    val costPerKm: Double
)

