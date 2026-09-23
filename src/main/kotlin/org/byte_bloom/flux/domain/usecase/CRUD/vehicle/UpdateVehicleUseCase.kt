package org.byte_bloom.flux.domain.usecase.CRUD.vehicle

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validator.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.vehicleValidation.VehicleUpdateValidator
import org.byte_bloom.flux.domain.validator.vehicleValidation.VehicleUpdateRequest

class UpdateVehicleUseCase(
    private val repository: VehicleRepository,
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: IdValidator = IdValidator(EntityPrefixes.VEHICLE),
    private val updateValidator: VehicleUpdateValidator
){
    suspend operator fun invoke(id: String , request: VehicleUpdateRequest): Result<Vehicle> {

        val idValidation = idValidator(id)
        if (idValidation is ValidationResult.Invalid) {
            return Result.failure(
                LogisticsException.ValidationException.EntityValidationException(
                    idValidation.errors.map { it.toString() }
                )
            )
        }

        val updateValidation = updateValidator(request)

        if (updateValidation is ValidationResult.Invalid) {
            return Result.failure(
                LogisticsException.ValidationException.EntityValidationException(
                    updateValidation.errors.map { it.toString() }
                )
            )
        }

        val existing = repository.getById(id).getOrElse { return Result.failure(it) }
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }

        val updated = existing.copy(
            currentHub = request.currentHubId?.let { warehousesById[it] } ?: existing.currentHub,
            maxCapacityKg = request.maxCapacityKg ?: existing.maxCapacityKg,
            costPerKm = request.costPerKm ?: existing.costPerKm
        )

        return repository.update(id, updated)

    }
}
