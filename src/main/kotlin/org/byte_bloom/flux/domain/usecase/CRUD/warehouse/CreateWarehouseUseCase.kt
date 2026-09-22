package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.warehouseValidations.WarehouseCreateValidator

class CreateWarehouseUseCase(
    private val repository: WarehouseRepository,
    private val validator: WarehouseCreateValidator
) {
    suspend operator fun invoke(warehouse: Warehouse): Warehouse {

        val validation = validator(warehouse)

        if (validation is ValidationResult.Invalid) {
            throw IllegalArgumentException(
                validation.errors.joinToString(", ")
            )
        }

        return repository.create(warehouse)
    }
}