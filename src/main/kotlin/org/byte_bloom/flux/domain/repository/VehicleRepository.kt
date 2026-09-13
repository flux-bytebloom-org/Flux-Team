package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

interface VehicleRepository {
    fun getAll(): List<Vehicle>

    /** Moves the vehicle to a new hub, updating fleet membership on both ends. */
    fun updateVehicleCurrentHub(vehicle: Vehicle, newHub: Warehouse): Vehicle
}

