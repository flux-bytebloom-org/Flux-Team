package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

interface VehicleRepository {
    fun getAll(): List<Vehicle>

    /** Updates the vehicle's hub AND registers it in that hub's fleet — two steps, one call. */
    fun updateVehicleCurrentHub(vehicle: Vehicle, newHub: Warehouse): Vehicle
}

