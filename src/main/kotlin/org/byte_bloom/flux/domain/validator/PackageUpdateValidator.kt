package org.byte_bloom.flux.domain.validator

class PackageUpdateValidator {

    operator fun invoke(
        weight: Double?,
        originHubId: String?,
        destinationHubId: String?,
        priority: String?
    ): ValidationResult {
        val errors = buildErrorList(weight, originHubId, destinationHubId, priority)

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    private fun buildErrorList(
        weight: Double?,
        originHubId: String?,
        destinationHubId: String?,
        priority: String?
    ): List<String> {
        return listOfNotNull(
            validateAtLeastOneField(weight, originHubId, destinationHubId, priority),
            validateWeightIfPresent(weight)
        )
    }

    private fun validateAtLeastOneField(
        weight: Double?,
        originHubId: String?,
        destinationHubId: String?,
        priority: String?
    ): String? {
        val allBlank = weight == null &&
                originHubId.isNullOrBlank() &&
                destinationHubId.isNullOrBlank() &&
                priority.isNullOrBlank()

        return if (allBlank) "At least one field must be provided for update" else null
    }

    private fun validateWeightIfPresent(weight: Double?): String? {
        return if (weight != null && weight <= 0.0) {
            "Weight must be a positive number"
        } else null
    }
}
