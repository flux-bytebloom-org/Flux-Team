package org.byte_bloom.flux.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleRequestDto(
    @SerialName("current_hub_id")
    val currentHubId: String,
    @SerialName("max_capacity_kg")
    val maxCapacityKg: Double,
    @SerialName("cost_per_km")
    val costPerKm: Double
)
