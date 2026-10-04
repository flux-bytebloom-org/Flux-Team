package org.byte_bloom.flux.domain.usecase.warehouse

import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validation.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validation.ValidationResult

class GetWarehouseByIdUseCase(
    private val repository: WarehouseRepository,
    private val idValidator: IdValidator = IdValidator(EntityPrefixes.WAREHOUSE)
) {
    suspend operator fun invoke(id: String): Result<Warehouse> {

        val validation = idValidator(id)

        if (validation is ValidationResult.Invalid) {
            throw IllegalArgumentException(
                validation.errors.joinToString(", ")
            )
        }

        return repository.getById(id)
    }
}