package org.byte_bloom.flux.data.datasource

import org.byte_bloom.flux.data.dataholders.WarehouseRaw

interface WarehouseDataSource {
    fun getAll(): List<WarehouseRaw>

    // LATER: methods for managing fleet/cargo queue persistence, e.g.:
    // fun addVehicleToFleet(hubId: String, vehicleId: String)
    // fun removeVehicleFromFleet(hubId: String, vehicleId: String)
    // fun addToHubQueue(packageId: String, hubId: String)
    // fun removeFromHubQueue(packageId: String, hubId: String)

}
