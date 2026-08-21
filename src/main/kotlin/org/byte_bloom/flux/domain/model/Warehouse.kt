package org.byte_bloom.flux.domain.model

import org.byte_bloom.flux.domain.logic.sorting.sortCargoByWeightDescending

data class Warehouse(
    val id: String,
    val name: String,
    val regionalZone: String,
    val latitude: Double,
    val longitude: Double
) {

    private val cargoQueue = mutableListOf<Package>()
    val outgoingRoutes = mutableListOf<Route>()
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

    fun fetchOutgoingRoutes(): List<Route> {
        return outgoingRoutes
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
