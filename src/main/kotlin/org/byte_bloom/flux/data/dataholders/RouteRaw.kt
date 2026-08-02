package org.byte_bloom.flux.data.dataholders

data class RouteRaw(
    val id: String,
    val originHub: String,
    val destinationHub: String,
    val distanceKm: Double,
    val typicalDelayMin: Double
)
