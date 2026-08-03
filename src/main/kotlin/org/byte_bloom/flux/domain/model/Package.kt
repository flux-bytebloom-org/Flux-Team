package org.byte_bloom.flux.domain.model


data class Package(
    val id: String,
    val weight: Double?,
    val originHub: Warehouse,
    val destinationHub: Warehouse,
    val priority: Priority
)
