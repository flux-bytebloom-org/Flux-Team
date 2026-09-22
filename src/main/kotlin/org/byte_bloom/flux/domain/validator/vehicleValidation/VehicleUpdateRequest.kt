package org.byte_bloom.flux.domain.validator.vehicleValidation

data class VehicleUpdateRequest(
    val currentHubId: String?,
    val maxCapacityKg: Double?,
    val costPerKm: Double?
)

