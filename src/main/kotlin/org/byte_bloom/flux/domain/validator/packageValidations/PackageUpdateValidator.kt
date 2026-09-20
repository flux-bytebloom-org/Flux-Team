package org.byte_bloom.flux.domain.validator.packageValidations

import org.byte_bloom.flux.domain.validator.EntityField
import org.byte_bloom.flux.domain.validator.FieldChecks
import org.byte_bloom.flux.domain.validator.ValidationField
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.Validator

class PackageUpdateValidator : Validator<PackageUpdateRequest> {

    override operator fun invoke(
        pkgUpdateReq: PackageUpdateRequest
    ): ValidationResult {
        val errors = listOfNotNull(
            FieldChecks.notNegative(EntityField.WEIGHT, pkgUpdateReq.weight),
            validateAtLeastOneField(pkgUpdateReq)
        )

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    private fun validateAtLeastOneField(
        request: PackageUpdateRequest
    ): ValidationField? = with(request) {
        val allBlank = weight == null &&
                originHubId.isNullOrBlank() &&
                destinationHubId.isNullOrBlank() &&
                priority.isNullOrBlank()

        if (allBlank) ValidationField.NoFieldUpdated else null
    }
}
