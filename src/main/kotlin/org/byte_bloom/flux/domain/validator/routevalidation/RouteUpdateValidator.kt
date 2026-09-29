package org.byte_bloom.flux.domain.validator.routevalidation

import org.byte_bloom.flux.domain.request.RouteUpdateRequest
import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.FieldChecks
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult
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
