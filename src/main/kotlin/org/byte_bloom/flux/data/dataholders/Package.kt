package org.byte_bloom.flux.data.dataholders

data class Package(
    val packageId: String,
    val weight: Double,
    val destinationHubId: String,
    val priority: Priority
)