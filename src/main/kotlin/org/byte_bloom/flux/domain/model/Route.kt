package org.byte_bloom.flux.domain.model

data class Route(
    val routeId: String,
    val originHub: String,
    val destinationHub: String,
    val distanceKm: Double,
    val typicalDelayMin: Double
)
//originHub, destinationHub ==> Warehouse