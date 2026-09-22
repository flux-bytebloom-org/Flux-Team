package org.byte_bloom.flux.domain.validator.vehicleValidation

import org.byte_bloom.flux.domain.validator.EntityField
import org.byte_bloom.flux.domain.validator.FieldChecks
import org.byte_bloom.flux.domain.validator.ValidationField
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.Validator

class UpdateVehicleValidator : Validator<VehicleUpdateRequest> {
    override operator fun invoke(value: VehicleUpdateRequest): ValidationResult {
        val errors = listOfNotNull(
            FieldChecks.notBlank(EntityField.CURRENT_HUB_ID, value.currentHubId),
            FieldChecks.positive(EntityField.MAX_CAPACITY_KG, value.maxCapacityKg),
            FieldChecks.positive(EntityField.COST_PER_KM, value.costPerKm),
            validateAtLeastOneField(value)
        )

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }

    }

    private fun validateAtLeastOneField(
        request: VehicleUpdateRequest
    ): ValidationField? = with(request) {
        val allBlank = currentHubId.isNullOrBlank() &&
                maxCapacityKg == null  &&
                costPerKm == null
        return if (allBlank) ValidationField.NoFieldUpdated() else null
    }
}
