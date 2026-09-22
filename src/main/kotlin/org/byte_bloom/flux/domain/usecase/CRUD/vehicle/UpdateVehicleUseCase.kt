package org.byte_bloom.flux.domain.usecase.CRUD.vehicle

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validator.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.packagevalidations.PackageUpdateValidator
import org.byte_bloom.flux.domain.validator.vehicleValidation.UpdateVehicleValidator
import org.byte_bloom.flux.domain.validator.vehicleValidation.VehicleUpdateRequest

class UpdateVehicleUseCase(
    private val repository: VehicleRepository,
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: IdValidator = IdValidator(EntityPrefixes.VEHICLE),
    private val updateValidator: UpdateVehicleValidator
){
    suspend operator fun invoke(id: String , request: VehicleUpdateRequest): Result<Vehicle>{

        val idValidation = idValidator(id)
        if (idValidation is ValidationResult.Invalid) {
            return Result.failure(IllegalArgumentException(idValidation.errors.joinToString(", ")))
        }

        val updateValidation = updateValidator(request)

        if (updateValidation is ValidationResult.Invalid) {
            throw IllegalArgumentException(
                updateValidation.errors.joinToString(", ")
            )
        }

        return runCatching {
            val existing = repository.getById(id)
            val warehousesById = warehouseRepository.getAll().associateBy { it.id }

            val updated = existing.copy(
                currentHub = request.currentHubId?.let { warehousesById[it] } ?: existing.currentHub,
                maxCapacityKg = request.maxCapacityKg ?: existing.maxCapacityKg,
                costPerKm = request.costPerKm ?: existing.costPerKm
            )

            repository.update(id, updated)
        }
    }
}
