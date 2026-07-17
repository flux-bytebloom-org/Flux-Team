package org.byte_bloom.flux.data.model


data class Route(
    val routeId: String,
    val destinationHubId: String,
    val distanceKm: Double,
    val typicalDelayMin: Double
)