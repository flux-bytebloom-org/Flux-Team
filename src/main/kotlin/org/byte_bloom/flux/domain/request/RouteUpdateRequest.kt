package org.byte_bloom.flux.domain.request

data class RouteUpdateRequest(
    val originHubId: String?,
    val destinationHubId: String?,
    val distanceKm: Double?,
    val typicalDelayMin: Double?
)