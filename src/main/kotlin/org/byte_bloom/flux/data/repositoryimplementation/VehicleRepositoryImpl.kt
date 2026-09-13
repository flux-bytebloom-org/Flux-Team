package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.datasource.VehicleDataSource
import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseFleet
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.VehicleRepository

class VehicleRepositoryImpl(
    private val vehicleDataSource: VehicleDataSource
) : VehicleRepository {

    override fun getAll(): List<Vehicle> =
        vehicleDataSource.getAll().map { it.toDomain() }


    override fun updateVehicleCurrentHub(vehicle: Vehicle, newHub: Warehouse): Vehicle {
        val oldHubId = vehicle.currentHub.id
        vehicleDataSource.updateCurrentHub(vehicle.id,oldHubId,newHub.id)
        val newVehicle = vehicle.copy(currentHub = newHub ) // TODO(delete)
        return newVehicle
    }
}

