package org.byte_bloom.flux.domain.validator

import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.FieldChecks
import org.byte_bloom.flux.domain.validation.ValidationResult

class IdValidator (
    private val expectedPrefix: String
): Validator<String>{
    override operator fun invoke(id: String): ValidationResult {
        val errors = listOfNotNull(
            FieldChecks.notBlank(EntityField.ID,id),
            FieldChecks.validPrefix(EntityField.ID,id,expectedPrefix)
        )
        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

}

