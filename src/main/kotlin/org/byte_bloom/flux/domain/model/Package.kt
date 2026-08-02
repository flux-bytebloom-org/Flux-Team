package org.byte_bloom.flux.domain.model

import org.byte_bloom.flux.data.dataholders.Priority

data class Package(
    val id: String,
    val weight: Double?,
    val originHubId: Warehouse,
    val destinationHubId: Warehouse,
    val priority: Priority
)
