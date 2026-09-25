package org.byte_bloom.flux.domain.response

import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Vehicle

data class GreedyDispatchResult(
    val selectedVehicles: List<Vehicle>,
    val uncoveredZones: Set<RegionalZone>
)