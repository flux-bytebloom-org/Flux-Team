package org.byte_bloom.flux.domain.response

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Vehicle

data class DispatchedVehicle(
    val vehicle: Vehicle,
    val loadedPackages: List<Package>,
    val totalWeight: Double
)
