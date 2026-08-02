package org.byte_bloom.flux.domain.model

data class Vehicle(
    val id: String,
    val currentHub: Warehouse,
    val maxCapacityKg: Double,
    val costPerKm: Double
)
