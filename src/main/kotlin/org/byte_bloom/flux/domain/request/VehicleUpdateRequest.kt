package org.byte_bloom.flux.domain.request

data class VehicleUpdateRequest(
    val currentHubId: String?,
    val maxCapacityKg: Double?,
    val costPerKm: Double?
)