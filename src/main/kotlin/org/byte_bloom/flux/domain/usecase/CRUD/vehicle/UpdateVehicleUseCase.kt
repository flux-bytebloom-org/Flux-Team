package org.byte_bloom.flux.domain.usecase.CRUD.vehicle

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.repository.VehicleRepository

class UpdateVehicleUseCase(
    private val repository: VehicleRepository
){
    suspend operator fun invoke(id: String , vehicle: Vehicle): Vehicle?{
        return repository.update(id,vehicle)
    }
}
