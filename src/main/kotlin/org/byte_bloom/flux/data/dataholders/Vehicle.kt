package org.byte_bloom.flux.data.dataholders

data class Vehicle(
    val vehicleId: String,
    val currentHubId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double
)