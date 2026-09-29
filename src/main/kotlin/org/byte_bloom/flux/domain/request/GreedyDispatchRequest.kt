package org.byte_bloom.flux.domain.request

import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.response.DispatchedVehicle

data class GreedyDispatchRequest(
    val targetZones: Set<RegionalZone>,
    val dispatchedVehicles: List<DispatchedVehicle>
)