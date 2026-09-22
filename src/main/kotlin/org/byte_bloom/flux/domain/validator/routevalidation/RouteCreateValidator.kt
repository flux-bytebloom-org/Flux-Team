package org.byte_bloom.flux.domain.validator.routevalidation

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.validator.EntityField
import org.byte_bloom.flux.domain.validator.FieldChecks
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.Validator

class RouteCreateValidator : Validator<Route> {

    override operator fun invoke(route: Route): ValidationResult {
        val errors = listOfNotNull(
            FieldChecks.notBlank(EntityField.ORIGIN_HUB_ID, route.originHub.id),
            FieldChecks.notBlank(EntityField.DESTINATION_HUB_ID, route.destinationHub.id),
            FieldChecks.positive(EntityField.DISTANCE_KM, route.distanceKm),
            FieldChecks.notNegative(EntityField.TYPICAL_DELAY_MIN, route.typicalDelayMin)
        )

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}
