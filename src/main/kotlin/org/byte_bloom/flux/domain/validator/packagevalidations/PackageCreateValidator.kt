package org.byte_bloom.flux.domain.validator.packagevalidations

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.FieldChecks
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.byte_bloom.flux.domain.validator.Validator

class PackageCreateValidator: Validator<Package> {

    override operator fun invoke(pkg: Package): ValidationResult {
        val errors = listOfNotNull(
            FieldChecks.notBlank(EntityField.ORIGIN_HUB_ID,pkg.originHub.id),
            FieldChecks.notBlank(EntityField.DESTINATION_HUB_ID,pkg.destinationHub.id),
            FieldChecks.positive(EntityField.WEIGHT,pkg.weight)
        )

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}
