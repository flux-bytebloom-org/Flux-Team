package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.validator.PackageIdValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

class GetPackageByIdUseCase(
    private val repository: PackageRepository,
    private val validator: PackageIdValidator
) {

    suspend operator fun invoke(id: String): Result<Package> {
        val validation = validator(id)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                IllegalArgumentException(validation.errors.joinToString(", "))
            )
        }

        return runCatching {
            repository.getById(id)
        }
    }
}

