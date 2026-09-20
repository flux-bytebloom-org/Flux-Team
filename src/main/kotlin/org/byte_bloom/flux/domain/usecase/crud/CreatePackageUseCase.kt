package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.validator.PackageCreateValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

class CreatePackageUseCase(
    private val repository: PackageRepository,
    private val validator: PackageCreateValidator
) {

    suspend operator fun invoke(pkg: Package): Result<Package> {
        val validation = validator(pkg)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                IllegalArgumentException(validation.errors.joinToString(", "))
            )
        }

        return runCatching {
            repository.create(pkg)
        }
    }
}

