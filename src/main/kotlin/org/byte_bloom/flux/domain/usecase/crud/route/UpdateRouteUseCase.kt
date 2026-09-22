package org.byte_bloom.flux.domain.usecase.crud.route

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validator.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.routevalidation.RouteUpdateRequest
import org.byte_bloom.flux.domain.validator.routevalidation.RouteUpdateValidator

class UpdateRouteUseCase(
    private val repository: RouteRepository,
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: IdValidator = IdValidator(EntityPrefixes.ROUTE),
    private val updateValidator: RouteUpdateValidator = RouteUpdateValidator()
) {
    suspend operator fun invoke(
        id: String,
        request: RouteUpdateRequest
    ): Result<Route> {

        val idValidation = idValidator(id)
        if (idValidation is ValidationResult.Invalid) {
            return Result.failure(
                LogisticsException.ValidationException.EntityValidationException(
                    idValidation.errors.map { it.toString() }
                )
            )
        }

        val validation = updateValidator(request)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                LogisticsException.ValidationException.EntityValidationException(
                    validation.errors.map { it.toString() }
                )
            )
        }

        val existing = repository.getById(id).getOrElse { error -> return Result.failure(error) }
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }

        val updatedRoute = existing.copy(
            originHub = request.originHubId?.let { warehousesById[it] } ?: existing.originHub,
            destinationHub = request.destinationHubId?.let { warehousesById[it] } ?: existing.destinationHub,
            distanceKm = request.distanceKm ?: existing.distanceKm,
            typicalDelayMin = request.typicalDelayMin ?: existing.typicalDelayMin
        )

        return repository.update(id, updatedRoute)
    }
}
