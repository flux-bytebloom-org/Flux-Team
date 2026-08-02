package org.byte_bloom.flux.data.dataholders

data class RouteRaw(
    val routeId: String,
    val originHub: String,
    val destinationHub: String,
    val distanceKm: Double,
    val typicalDelayMin: Double
)
//originHub, destinationHub ==> Warehouse
