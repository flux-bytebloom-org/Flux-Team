package org.byte_bloom.flux.domain.validator

private const val PACKAGE_ID_PREFIX = "PKG-"

class PackageIdValidator {

    operator fun invoke(id: String): ValidationResult {
        val errors = buildErrorList(id)

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    private fun buildErrorList(id: String): List<String> {
        return listOfNotNull(
            validateNotBlank(id),
            validatePrefix(id)
        )
    }

    private fun validateNotBlank(id: String): String? {
        return if (id.isBlank()) "Id must not be blank" else null
    }

    private fun validatePrefix(id: String): String? {
        return if (id.isNotBlank() && !id.startsWith(PACKAGE_ID_PREFIX)) {
            "Id must start with '$PACKAGE_ID_PREFIX'"
        } else null
    }
}
