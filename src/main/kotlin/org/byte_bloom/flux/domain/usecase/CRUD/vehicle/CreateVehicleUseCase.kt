package org.byte_bloom.flux.domain.usecase.CRUD.vehicle

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.vehicleValidation.VehicleCreateValidator

class CreateVehicleUseCase(
    private val repository: VehicleRepository,
    private val validator: VehicleCreateValidator
) {
    suspend operator fun invoke(vehicle: Vehicle) :  Result<Vehicle> {
        val validation = validator(vehicle)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                IllegalArgumentException(validation.errors.joinToString(", "))
            )
        }


        return runCatching{
            repository.create(vehicle)
        }
    }
}
