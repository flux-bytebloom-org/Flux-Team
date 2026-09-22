package org.byte_bloom.flux.domain.usecase.crud.pakage

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.validator.packagevalidations.PackageCreateValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

class CreatePackageUseCase(
    private val repository: PackageRepository,
    private val validator: PackageCreateValidator
) {

    suspend operator fun invoke(pkg: Package): Result<Package> {
        val validation = validator(pkg)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                LogisticsException.ValidationException.EntityValidationException(
                    validation.errors.map { it.toString() }
                )
            )
        }

        return repository.create(pkg)
        }
    }


