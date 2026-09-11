package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Vehicle

interface VehicleRepository {
    fun getAll(): List<Vehicle>

    /** Updates the vehicle's hub AND registers it in that hub's fleet — two steps, one call. */
    fun updateVehicleCurrentHub(vehicleId: String, hubId: String): Vehicle
}

