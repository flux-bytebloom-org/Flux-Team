package org.byte_bloom.flux.domain.model

data class Warehouse(
    val warehouseId: String,
    val warehouseName: String,
    val regionalZone: String,
    val cargoQueue: MutableList<Package> = mutableListOf(),
    val outgoingRoutes: MutableList<Route> = mutableListOf(),
    val stationedVehicles: MutableList<Vehicle> = mutableListOf()
)
