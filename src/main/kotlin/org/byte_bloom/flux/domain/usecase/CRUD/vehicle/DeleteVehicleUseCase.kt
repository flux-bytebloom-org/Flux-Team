package org.byte_bloom.flux.domain.usecase.CRUD.vehicle

import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.validator.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

class DeleteVehicleUseCase(
    private val repository: VehicleRepository,
    private val validator: IdValidator = IdValidator(EntityPrefixes.VEHICLE)

) {
    suspend operator fun invoke(id: String) : Result<Unit> {
        val validation = validator(id)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                IllegalArgumentException(validation.errors.joinToString(", "))
            )
        }

        return runCatching {
            repository.delete(id)
        }
    }
}
