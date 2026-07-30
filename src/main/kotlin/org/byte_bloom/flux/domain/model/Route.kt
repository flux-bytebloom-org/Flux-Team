package org.byte_bloom.flux.domain.model

data class Route(
    val routeId: String,
    val originHub: Warehouse,
    val destinationHub: Warehouse,
    val distanceKm: Double,
    val typicalDelayMin: Double
)