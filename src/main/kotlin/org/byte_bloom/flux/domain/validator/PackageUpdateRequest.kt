package org.byte_bloom.flux.domain.validator

data class PackageUpdateRequest(
    val weight: Double?,
    val originHub: String?,
    val destinationHub: String?,
    val priority: String?
)
