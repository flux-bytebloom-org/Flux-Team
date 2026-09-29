package org.byte_bloom.flux.domain.request

data class PackageUpdateRequest(
    val weight: Double?,
    val originHubId: String?,
    val destinationHubId: String?,
    val priority: String?
)