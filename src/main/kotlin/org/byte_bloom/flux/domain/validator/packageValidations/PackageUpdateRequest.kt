package org.byte_bloom.flux.domain.validator.packageValidations

data class PackageUpdateRequest(
    val weight: Double?,
    val originHubId: String?,
    val destinationHubId: String?,
    val priority: String?
)
