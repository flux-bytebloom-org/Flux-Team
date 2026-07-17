package org.byte_bloom.flux.data.model



data class Fleet(
    val vehicleId: String,
    val currentHubId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double
)