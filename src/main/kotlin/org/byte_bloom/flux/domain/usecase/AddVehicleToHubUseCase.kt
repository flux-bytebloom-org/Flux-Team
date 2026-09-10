package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

class AddVehicleToHubUseCase (
    val vehicleRepo: VehicleRepository,
    val warehouseRepo: WarehouseRepository
){
    operator fun invoke(hub: Warehouse, vehicle: Vehicle): Vehicle {
        val updatedVehicle = vehicleRepo.updateVehicleCurrentHub(vehicle.id, hub.id)
        warehouseRepo.updateWarehouseFleetListWithCurrentVehicle(hub.id, vehicle.id)


        /* old code
        val updatedVehicle = vehicle.copy(currentHub = hub)
        hub.addVehicle(updatedVehicle)
        return updatedVehicle
         */
        return updatedVehicle
    }
}

