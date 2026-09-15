package org.byte_bloom.flux.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PackageResponseDto(
    val id: String,
    val weight: Double?,
    @SerialName("origin_hub_id")
    val originHubId: String,
    @SerialName("destination_hub_id")
    val destinationHubId: String,
    val priority: String
)

