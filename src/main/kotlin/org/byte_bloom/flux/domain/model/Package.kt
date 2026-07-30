package org.byte_bloom.flux.domain.model

data class Package(
    val Packageid: String,
    val weight: Double?,
    val originHubId: Warehouse,
    val destinationHubId: Warehouse,
    val priority: Priority
)