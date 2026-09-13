package org.byte_bloom.flux.data.datasource

import org.byte_bloom.flux.data.dataholders.VehicleRaw

interface VehicleDataSource {
    fun getAll(): List<VehicleRaw>

    /** Updates the vehicle's currentHub, moving it out of the old hub's fleet
     *  and into the new hub's fleet. */
    fun updateCurrentHub(vehicleId: String, oldHubId: String, newHubId: String)
}
