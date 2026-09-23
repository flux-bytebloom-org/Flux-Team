package org.byte_bloom.flux.domain.usecase.crud.pakage

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validator.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validator.packagevalidations.PackageUpdateRequest
import org.byte_bloom.flux.domain.validator.packagevalidations.PackageUpdateValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

class UpdatePackageUseCase(
    private val repository: PackageRepository,
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: IdValidator = IdValidator(EntityPrefixes.PACKAGE),
    private val updateValidator: PackageUpdateValidator
) {
    suspend operator fun invoke(id: String, request: PackageUpdateRequest): Result<Package> {

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

            val existing = repository.getById(id).getOrElse { error -> return Result.failure(error) }
            val warehousesById = warehouseRepository.getAll().associateBy { it.id }

            val updated = existing.copy(
                weight = request.weight ?: existing.weight,
                originHub = request.originHubId?.let { warehousesById[it] } ?: existing.originHub,
                destinationHub = request.destinationHubId?.let { warehousesById[it] } ?: existing.destinationHub,
                priority = request.priority?.let { Priority.valueOf(it.uppercase()) } ?: existing.priority
            )

        return repository.update(id, updated)
        }
    }




