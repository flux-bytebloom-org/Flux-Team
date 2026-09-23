package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.remote.datasource.RemoteVehicleDataSource
import org.byte_bloom.flux.data.remote.dto.toDomain
import org.byte_bloom.flux.data.remote.dto.toRequestDto
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.data.csv.datasource.VehicleDataSource as LocalVehicleDataSource
import org.byte_bloom.flux.data.csv.mapper.toDomain as toDomainLocal
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class VehicleRepositoryImpl(
    private val localDataSource: LocalVehicleDataSource,
    private val remoteDataSource: RemoteVehicleDataSource,
    private val warehouseRepository: WarehouseRepository
) : VehicleRepository {

    override suspend fun getAll(): List<Vehicle> {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return localDataSource.getAll()
            .mapNotNull { it.toDomainLocal(warehousesById) }
            .onEach { vehicle -> vehicle.currentHub.addVehicle(vehicle) }
    }

    override suspend fun getById(id: String): Result<Vehicle> = runCatching{
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        remoteDataSource.getById(id)?.toDomain(warehousesById)
            ?:throw LogisticsException.EntityNotFoundException.VehicleNotFoundException(id)

    }

    override suspend fun create(vehicle: Vehicle): Result<Vehicle> = runCatching {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        val response  = remoteDataSource.create(vehicle.toRequestDto())
        response.toDomain(warehousesById)
            ?: throw LogisticsException.EntityNotFoundException.VehicleNotFoundException(response.currentHubId)
    }

    override suspend fun update(id: String, vehicle: Vehicle): Result<Vehicle> = runCatching {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        val response  = remoteDataSource.update(id, vehicle.toRequestDto())
        response.toDomain(warehousesById)
            ?: throw LogisticsException.EntityNotFoundException.VehicleNotFoundException(response.currentHubId)
    }

    override suspend fun delete(id: String) : Result<Unit> =runCatching {  remoteDataSource.delete(id)}



    override fun updateVehicleCurrentHub(vehicle: Vehicle, newHub: Warehouse): Vehicle {
        val oldHubId = vehicle.currentHub.id
        localDataSource.updateCurrentHub(vehicle.id,oldHubId,newHub.id)
        val newVehicle = vehicle.copy(currentHub = newHub ) // TODO(delete)
        return newVehicle
    }
}

