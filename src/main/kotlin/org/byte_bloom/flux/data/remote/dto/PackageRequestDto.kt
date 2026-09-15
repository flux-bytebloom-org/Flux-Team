package org.byte_bloom.flux.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class PackageRequestDto(
    val weight: Double?,
    val origin_hub_id: String,
    val destination_hub_id: String,
    val priority: String
)

