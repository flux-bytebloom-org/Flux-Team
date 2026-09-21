package org.byte_bloom.flux.domain.usecase.crud.route

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validator.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validator.routeValidation.RouteUpdateRequest
import org.byte_bloom.flux.domain.validator.routeValidation.RouteUpdateValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

class UpdateRouteUseCase(
    private val repository: RouteRepository,
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: IdValidator = IdValidator(EntityPrefixes.ROUTE),
    private val updateValidator: RouteUpdateValidator
) {

    suspend operator fun invoke(
        id: String,
        originHubId: String?,
        destinationHubId: String?,
        distanceKm: Double?,
        typicalDelayMin: Double?
    ): Result<Route> {

        val idValidation = idValidator(id)
        if (idValidation is ValidationResult.Invalid) {
            return Result.failure(IllegalArgumentException(idValidation.errors.joinToString(", ")))
        }

        val routeUpdateRequest = RouteUpdateRequest(originHubId, destinationHubId, distanceKm, typicalDelayMin)

        val updateValidation = updateValidator(routeUpdateRequest)
        if (updateValidation is ValidationResult.Invalid) {
            return Result.failure(IllegalArgumentException(updateValidation.errors.joinToString(", ")))
        }

        return runCatching {
            val existing = requireNotNull(repository.getById(id)) { "Route $id not found" }
            val warehousesById = warehouseRepository.getAll().associateBy { it.id }

            val updated = existing.copy(
                originHub = originHubId?.let { warehousesById[it] } ?: existing.originHub,
                destinationHub = destinationHubId?.let { warehousesById[it] } ?: existing.destinationHub,
                distanceKm = distanceKm ?: existing.distanceKm,
                typicalDelayMin = typicalDelayMin ?: existing.typicalDelayMin
            )

            repository.update(id, updated)
        }
    }
}
