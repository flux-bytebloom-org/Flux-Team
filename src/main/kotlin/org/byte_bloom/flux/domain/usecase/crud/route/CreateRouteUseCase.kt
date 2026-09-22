package org.byte_bloom.flux.domain.usecase.crud.route

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.routevalidation.RouteCreateValidator

class CreateRouteUseCase(
    private val repository: RouteRepository,
    private val validator: RouteCreateValidator
) {

    suspend operator fun invoke(route: Route): Result<Route> {
        val validation = validator(route)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                IllegalArgumentException(validation.errors.joinToString(", "))
            )
        }

        return runCatching {
            repository.create(route)
        }
    }
}
