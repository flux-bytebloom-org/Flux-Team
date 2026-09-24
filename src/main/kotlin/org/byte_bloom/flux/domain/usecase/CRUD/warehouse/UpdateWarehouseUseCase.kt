package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validator.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.warehouseValidations.WarehouseUpdateRequest
import org.byte_bloom.flux.domain.validator.warehouseValidations.WarehouseUpdateValidator
import kotlin.text.uppercase

class UpdateWarehouseUseCase(
    private val repository: WarehouseRepository,
    private val idValidator: IdValidator = IdValidator(EntityPrefixes.WAREHOUSE),
    private val validator: WarehouseUpdateValidator = WarehouseUpdateValidator()
) {
    suspend operator fun invoke(
        id: String,
        request: WarehouseUpdateRequest
    ): Result<Warehouse> {

        val idValidation = idValidator(id)
        if (idValidation is ValidationResult.Invalid) {
            return Result.failure(
                LogisticsException.ValidationException.EntityValidationException(
                    idValidation.errors.map { it.toString() }
                )
            )
        }

        val validation = validator(request)

        if (validation is ValidationResult.Invalid) {
            throw IllegalArgumentException(
                validation.errors.joinToString(", ")
            )
        }

        val existing = repository.getById(id).getOrElse { error -> return Result.failure(error) }
            ?: throw IllegalArgumentException("Warehouse not found: $id")

        val updatedWarehouse = existing.copy(
            name = request.name ?: existing.name,
            regionalZone = request.regionalZone?.let{ RegionalZone.valueOf(it.uppercase())}?: existing.regionalZone,
            latitude = request.latitude ?: existing.latitude,
            longitude = request.longitude ?: existing.longitude
        )

        return repository.update(id, updatedWarehouse)
    }
}
