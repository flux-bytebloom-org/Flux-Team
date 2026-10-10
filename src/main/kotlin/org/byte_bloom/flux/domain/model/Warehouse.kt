package org.byte_bloom.flux.domain.model

data class Warehouse(
    val id: String,
    val name: String,
    val regionalZone: RegionalZone,
    val latitude: Double,
    val longitude: Double

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

    fun removePackage(packageItem: Package) {
        cargoQueue.remove(packageItem)
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

    fun removeVehicle(vehicle: Vehicle) {
        stationedVehicles.remove(vehicle)
    }
}

