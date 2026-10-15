package org.byte_bloom.flux.domain.model

data class CargoRecord(
    val latitude: Double,
    val longitude: Double,
    val weightKg: Double,
    val sector: RegionalZone
)
