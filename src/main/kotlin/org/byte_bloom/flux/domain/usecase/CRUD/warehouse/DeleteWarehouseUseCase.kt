package org.byte_bloom.flux.domain.usecase.crud.warehouse

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validator.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

class DeleteWarehouseUseCase(
    private val repository: WarehouseRepository,
    private val idValidator: IdValidator = IdValidator(EntityPrefixes.WAREHOUSE)
) {

    suspend operator fun invoke(id: String): Result<Unit> {

        val validation = idValidator(id)
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

