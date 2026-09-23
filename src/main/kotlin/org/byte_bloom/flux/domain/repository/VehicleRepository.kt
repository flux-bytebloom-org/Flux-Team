package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

interface VehicleRepository {
    suspend fun getAll(): List<Vehicle>

    suspend fun getById(id: String): Result<Vehicle>
    suspend fun create(vehicle: Vehicle): Result<Vehicle>
    suspend fun update(id: String, vehicle: Vehicle): Result<Vehicle>
    suspend fun delete(id: String): Result<Unit>

    /*suspend fun getById(id: String): Result<Vehicle>
    suspend fun create(vehicle: Vehicle): Result<Vehicle>
    suspend fun update(id: String, vehicle: Vehicle): Result<Vehicle>
    suspend fun delete(id: String): Result<Unit>*/

    /** Moves the vehicle to a new hub, updating fleet membership on both ends. */
    fun updateVehicleCurrentHub(vehicle: Vehicle, newHub: Warehouse): Vehicle
}

