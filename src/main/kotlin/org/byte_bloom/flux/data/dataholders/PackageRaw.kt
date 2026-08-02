package org.byte_bloom.flux.data.dataholders

data class PackageRaw(
    val packageId: String,
    val weight: Double?,
    val originHubId: String,
    val destinationHubId: String,
    val priority: Priority
)
// originHubId and destinationHubId ==> warehouse
