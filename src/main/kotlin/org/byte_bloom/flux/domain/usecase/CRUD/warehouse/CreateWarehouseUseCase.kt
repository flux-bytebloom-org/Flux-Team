package org.byte_bloom.flux.domain.usecase.crud.warehouse

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.warehouseValidations.WarehouseCreateValidator

class CreateWarehouseUseCase(
    private val repository: WarehouseRepository,
    private val validator: WarehouseCreateValidator
) {
    suspend operator fun invoke(warehouse: Warehouse): Result<Warehouse> {

        val validation = validator(warehouse)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                LogisticsException.ValidationException.EntityValidationException(
                    validation.errors.map { it.toString() }
                )
            )
        }

        return repository.create(warehouse)
    }
}