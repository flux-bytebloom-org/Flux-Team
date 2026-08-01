package org.byte_bloom.flux.domain.model

import org.byte_bloom.flux.logic.sorters.sortCargoByWeightDescending

data class Warehouse(
    val warehouseId: String,
    val warehouseName: String,
    val regionalZone: String,
    val cargoQueue: MutableList<Package> = mutableListOf(),
    val outgoingRoutes: MutableList<Route> = mutableListOf(),
    val stationedVehicles: MutableList<Vehicle> = mutableListOf()
){
    /**
     * Sorts cargoQueue in descending order of package weight using Quicksort.
     */
    fun sortCargoQueue() {
        sortCargoByWeightDescending(cargoQueue)
    }
}
