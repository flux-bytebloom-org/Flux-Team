package org.byte_bloom.flux.dataholders

data class Package(
    val id: String,
    val weight: Double,
    val destinationHubId: String,
    val priority: Priority
)