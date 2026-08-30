package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Warehouse

class GetHubCongestionRatioUseCase {

    operator fun invoke(hub: Warehouse): Double {
        val totalCargoWeight = hub.getCargoQueue().sumOf { it.weight ?: 0.0 }
        val totalFleetCapacity = hub.getStationedVehicles().sumOf { it.maxCapacityKg }

        if (totalFleetCapacity == 0.0) {
            return if (totalCargoWeight > 0.0) Double.POSITIVE_INFINITY else 0.0
        }

        return totalCargoWeight / totalFleetCapacity
    }
}
