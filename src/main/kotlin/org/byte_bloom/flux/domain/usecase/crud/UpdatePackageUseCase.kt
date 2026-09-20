package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validator.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validator.packageValidations.PackageUpdateRequest
import org.byte_bloom.flux.domain.validator.packageValidations.PackageUpdateValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

class UpdatePackageUseCase(
    private val repository: PackageRepository,
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: IdValidator = IdValidator(EntityPrefixes.PACKAGE),
    private val updateValidator: PackageUpdateValidator
) {

    suspend operator fun invoke(
        id: String,
        weight: Double?,
        originHubId: String?,
        destinationHubId: String?,
        priority: String?
    ): Result<Package> {

        val idValidation = idValidator(id)
        if (idValidation is ValidationResult.Invalid) {
            return Result.failure(IllegalArgumentException(idValidation.errors.joinToString(", ")))
        }

        val PkgUpdateRequest = PackageUpdateRequest(weight, originHubId, destinationHubId, priority)

        val updateValidation = updateValidator(PkgUpdateRequest)
        if (updateValidation is ValidationResult.Invalid) {
            return Result.failure(IllegalArgumentException(updateValidation.errors.joinToString(", ")))
        }

        return runCatching {
            val existing = repository.getById(id)
            val warehousesById = warehouseRepository.getAll().associateBy { it.id }

            val updated = existing.copy(
                weight = weight ?: existing.weight,
                originHub = originHubId?.let { warehousesById[it] } ?: existing.originHub,
                destinationHub = destinationHubId?.let { warehousesById[it] } ?: existing.destinationHub,
                priority = priority?.let { Priority.valueOf(it.uppercase()) } ?: existing.priority
            )

            repository.update(id, updated)
        }
    }
}



