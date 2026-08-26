package org.byte_bloom.flux.data.dataholders

data class VehicleRaw(
    val id: String,
    val currentHubId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double
)
