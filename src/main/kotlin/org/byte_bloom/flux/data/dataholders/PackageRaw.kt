package org.byte_bloom.flux.data.dataholders

data class PackageRaw(
    val id: String,
    val weight: Double?,
    val originHubId: String,
    val destinationHubId: String,
    val priority: Priority
)

