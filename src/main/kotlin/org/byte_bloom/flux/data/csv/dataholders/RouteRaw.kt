package org.byte_bloom.flux.data.csv.dataholders

data class RouteRaw(
    val id: String,
    val originHubId: String,
    val destinationHubId: String,
    val distanceKm: Double,
    val typicalDelayMin: Double
)
