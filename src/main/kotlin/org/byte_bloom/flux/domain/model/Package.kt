package org.byte_bloom.flux.domain.model

data class Package(
    val packageId: String,
    val weight: Double?,
    val originHubId: String,
    val destinationHubId: String,
    val priority: Priority
)
// originHubId and destinationHubId ==> warehouse
