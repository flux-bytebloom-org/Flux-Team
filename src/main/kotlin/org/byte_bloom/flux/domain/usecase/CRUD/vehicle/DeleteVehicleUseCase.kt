package org.byte_bloom.flux.domain.usecase.CRUD.vehicle

import org.byte_bloom.flux.domain.repository.VehicleRepository

class DeleteVehicleUseCase(
    private val repository: VehicleRepository
) {
    suspend operator fun invoke(id: String) {
        repository.delete(id)
    }
}
