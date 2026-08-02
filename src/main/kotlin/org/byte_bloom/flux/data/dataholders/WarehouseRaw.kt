package org.byte_bloom.flux.data.dataholders

import org.byte_bloom.flux.domain.operations.sorting.sortCargoByWeightDescending

class WarehouseRaw(
    val warehouseId: String,
    val name: String,
    val regionalZone: String,
    val latitude: Double,
    val longitude: Double
) {

    private val cargoQueue = mutableListOf<PackageRaw>()
    private val outgoingRoutes = mutableListOf<RouteRaw>()
    private val stationedVehicles = mutableListOf<VehicleRaw>()


    fun addPackage(packageItem: PackageRaw) {
        cargoQueue.add(packageItem)
    }

    fun getCargoQueue(): List<PackageRaw> {
        return cargoQueue.toList()
    }


    fun addRoute(route: RouteRaw) {
        outgoingRoutes.add(route)
    }

    fun getOutgoingRoutes(): List<RouteRaw> {
        return outgoingRoutes.toList()
    }


    fun addVehicle(vehicle: VehicleRaw) {
        stationedVehicles.add(vehicle)
    }

    fun getStationedVehicles(): List<VehicleRaw> {
        return stationedVehicles.toList()
    }
    
    fun sortCargoQueue() {
        sortCargoByWeightDescending(cargoQueue)
    }

    
}
