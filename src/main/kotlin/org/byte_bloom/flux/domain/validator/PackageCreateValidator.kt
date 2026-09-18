package org.byte_bloom.flux.domain.validator

import org.byte_bloom.flux.domain.model.Package

class PackageCreateValidator {

    operator fun invoke(pkg: Package): ValidationResult {
        val errors = buildErrorList(pkg)

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    private fun buildErrorList(pkg: Package): List<String> {
        return listOfNotNull(
            validateOriginHub(pkg),
            validateDestinationHub(pkg),
            validateWeight(pkg)
        )
    }

    private fun validateOriginHub(pkg: Package): String? {
        return if (pkg.originHub.id.isBlank()) {
            "Origin hub id must not be blank"
        } else null
    }

    private fun validateDestinationHub(pkg: Package): String? {
        return if (pkg.destinationHub.id.isBlank()) {
            "Destination hub id must not be blank"
        } else null
    }

    private fun validateWeight(pkg: Package): String? {
        val weight = pkg.weight
        return if (weight != null && weight <= 0.0) {
            "Weight must be a positive number"
        } else null
    }
}
