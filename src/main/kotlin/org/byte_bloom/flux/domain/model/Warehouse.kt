package org.byte_bloom.flux.domain.model

import org.byte_bloom.flux.logic.sorters.sortCargoByWeightDescending

class Warehouse(
    val warehouseId: String,
    val name: String,
    val regionalZone: String
) {

    private val cargoQueue = mutableListOf<Package>()
    private val outgoingRoutes = mutableListOf<Route>()
    private val stationedVehicles = mutableListOf<Vehicle>()


    fun addPackage(packageItem: Package) {
        cargoQueue.add(packageItem)
    }

    fun getCargoQueue(): List<Package> {
        return cargoQueue.toList()
    }


    fun addRoute(route: Route) {
        outgoingRoutes.add(route)
    }

    fun getOutgoingRoutes(): List<Route> {
        return outgoingRoutes.toList()
    }


    fun addVehicle(vehicle: Vehicle) {
        stationedVehicles.add(vehicle)
    }

    fun getStationedVehicles(): List<Vehicle> {
        return stationedVehicles.toList()
    }
    
    fun sortCargoQueue() {
        sortCargoByWeightDescending(cargoQueue)
    }

    
}
