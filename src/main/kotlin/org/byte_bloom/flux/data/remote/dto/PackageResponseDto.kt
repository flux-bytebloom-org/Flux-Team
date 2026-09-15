package org.byte_bloom.flux.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PackageResponseDto(
    val id: String,
    val weight: Double?,
    val origin_hub_id: String,
    val destination_hub_id: String,
    val priority: String
)
