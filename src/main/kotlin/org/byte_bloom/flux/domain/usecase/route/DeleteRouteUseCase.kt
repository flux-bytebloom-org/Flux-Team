package org.byte_bloom.flux.domain.usecase.route

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.validation.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validation.ValidationResult

class DeleteRouteUseCase(
    private val repository: RouteRepository,
    private val idValidator: IdValidator = IdValidator(EntityPrefixes.ROUTE)
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
