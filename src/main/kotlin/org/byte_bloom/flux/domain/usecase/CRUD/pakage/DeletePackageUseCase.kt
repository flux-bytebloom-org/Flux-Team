package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.validator.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

private const val PACKAGE_ID_PREFIX = "PKG-"

class DeletePackageUseCase(
    private val repository: PackageRepository,
    private val validator: IdValidator = IdValidator(EntityPrefixes.PACKAGE)
) {

    suspend operator fun invoke(id: String): Result<Unit> {
        val validation = validator(id)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                LogisticsException.ValidationException.EntityValidationException(
                    validation.errors.map { it.toString() }
                )
            )
        }

        return repository.delete(id)
        }
    }

