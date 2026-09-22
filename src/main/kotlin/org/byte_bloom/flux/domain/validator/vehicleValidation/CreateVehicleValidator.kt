package org.byte_bloom.flux.domain.validator.vehicleValidation

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.validator.EntityField
import org.byte_bloom.flux.domain.validator.FieldChecks
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.Validator

class CreateVehicleValidator: Validator<Vehicle>{
    override fun invoke(vehicle: Vehicle): ValidationResult {
        val errors = listOfNotNull(
            FieldChecks.notBlank(EntityField.CURRENT_HUB_ID, vehicle.currentHub.id),
            FieldChecks.positive(EntityField.MAX_CAPACITY_KG, vehicle.maxCapacityKg),
            FieldChecks.positive(EntityField.COST_PER_KM, vehicle.costPerKm)
            )

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}
