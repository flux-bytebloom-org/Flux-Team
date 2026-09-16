package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.csv.datasource.VehicleDataSource
import org.byte_bloom.flux.data.csv.mapper.toDomain
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class VehicleRepositoryImpl(
    private val vehicleDataSource: VehicleDataSource,private val warehouseRepository: WarehouseRepository
) : VehicleRepository {

    override suspend fun getAll(): List<Vehicle> {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return vehicleDataSource.getAll()
            .mapNotNull { it.toDomain(warehousesById) }
            .onEach { vehicle -> vehicle.currentHub.addVehicle(vehicle) }
    }


    override fun updateVehicleCurrentHub(vehicle: Vehicle, newHub: Warehouse): Vehicle {
        val oldHubId = vehicle.currentHub.id
        vehicleDataSource.updateCurrentHub(vehicle.id,oldHubId,newHub.id)
        val newVehicle = vehicle.copy(currentHub = newHub ) // TODO(delete)
        return newVehicle
    }
}

