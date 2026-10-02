package org.byte_bloom.flux.domain.usecase.crud.vehicle

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validation.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.byte_bloom.flux.domain.validator.vehicleValidation.VehicleUpdateValidator
import org.byte_bloom.flux.domain.request.VehicleUpdateRequest

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

        val newHub = request.currentHubId?.let { hubId ->
            warehousesById[hubId] ?: return Result.failure(
                LogisticsException.EntityNotFoundException.WarehouseNotFoundException(hubId)
            )
        } ?: existing.currentHub

        val updated = existing.copy(
            currentHub = newHub,
            maxCapacityKg = request.maxCapacityKg ?: existing.maxCapacityKg,
            costPerKm = request.costPerKm ?: existing.costPerKm
        )

        return repository.update(id, updated)

    }
}
