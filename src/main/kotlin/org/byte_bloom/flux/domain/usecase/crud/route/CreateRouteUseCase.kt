package org.byte_bloom.flux.domain.usecase.crud.route

import org.byte_bloom.flux.domain.exception.LogisticsException
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
                LogisticsException.ValidationException.EntityValidationException(
                    validation.errors.map { it.toString() }
                )
            )
        }

        return repository.create(route)
    }
}
