package org.byte_bloom.flux.domain.usecase.crud.route

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.validator.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

class GetRouteByIdUseCase(
    private val repository: RouteRepository,
    private val validator: IdValidator = IdValidator(EntityPrefixes.ROUTE)
) {

    suspend operator fun invoke(id: String): Result<Route?> {
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
