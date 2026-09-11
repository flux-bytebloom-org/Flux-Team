package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

interface VehicleRepository {
    fun getAll(): List<Vehicle>

    /** removes the vehicle from its old hub fleet
     * AND Updates it's hub
     * AND registers it in that hub's fleet — three steps, one call. */
    fun updateVehicleCurrentHub(vehicle: Vehicle, newHub: Warehouse): Vehicle
}

