package org.byte_bloom.flux.domain.validator.routeValidation

import org.byte_bloom.flux.domain.validator.EntityField
import org.byte_bloom.flux.domain.validator.FieldChecks
import org.byte_bloom.flux.domain.validator.ValidationField
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.Validator

class RouteUpdateValidator : Validator<RouteUpdateRequest> {

    override operator fun invoke(routeUpdateReq: RouteUpdateRequest): ValidationResult {
        val errors = listOfNotNull(
            FieldChecks.positive(EntityField.DISTANCE_KM, routeUpdateReq.distanceKm),
            FieldChecks.notNegative(EntityField.TYPICAL_DELAY_MIN, routeUpdateReq.typicalDelayMin),
            validateAtLeastOneField(routeUpdateReq)
        )

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    private fun validateAtLeastOneField(request: RouteUpdateRequest): ValidationField? = with(request) {
        val allBlank = originHubId.isNullOrBlank() &&
                destinationHubId.isNullOrBlank() &&
                distanceKm == null &&
                typicalDelayMin == null
        return if (allBlank) ValidationField.NoFieldUpdated() else null
    }
}
