package org.byte_bloom.flux.domain.validator

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

