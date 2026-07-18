package org.byte_bloom.flux.dataholders

data class Fleet(
    val vehicleId: String,
    val currentHubId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double
)