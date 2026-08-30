package org.byte_bloom.flux.data.dataholders

data class RouteRaw(
    val id: String,
    val originHubId: String,
    val destinationHubId: String,
    val distanceKm: Double,
    val typicalDelayMin: Double
)

