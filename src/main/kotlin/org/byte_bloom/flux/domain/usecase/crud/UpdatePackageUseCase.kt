package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.data.remote.datasource.RemotePackageDataSource
import org.byte_bloom.flux.data.remote.dto.PackageRequestDto
import org.byte_bloom.flux.data.remote.dto.toDomain
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.validator.PackageIdValidator
import org.byte_bloom.flux.domain.validator.PackageUpdateValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

class UpdatePackageUseCase(
    private val dataSource: RemotePackageDataSource,
    private val idValidator: PackageIdValidator,
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

        val updateValidation = updateValidator(weight, originHubId, destinationHubId, priority)
        if (updateValidation is ValidationResult.Invalid) {
            return Result.failure(IllegalArgumentException(updateValidation.errors.joinToString(", ")))
        }

        return runCatching {
            val existing = dataSource.getById(id)

            val requestDto = PackageRequestDto(
                weight = weight ?: existing.weight,
                originHubId = originHubId ?: existing.originHubId,
                destinationHubId = destinationHubId ?: existing.destinationHubId,
                priority = priority ?: existing.priority
            )
            dataSource.update(id, requestDto).toDomain()
        }
    }
}


