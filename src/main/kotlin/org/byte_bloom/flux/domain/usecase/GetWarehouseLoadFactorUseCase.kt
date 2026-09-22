package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Warehouse

class GetWarehouseLoadFactorUseCase {

    operator fun invoke(warehouse: Warehouse): Double {

        val totalFleetCapacityKg = warehouse.getStationedVehicles()
            .sumOf { it.maxCapacityKg }

        if (totalFleetCapacityKg <= 0.0) {
            throw LogisticsException.BusinessLogicException.NoStationedVehiclesException(warehouse.id)
        }

        val totalQueueWeightKg = warehouse.getCargoQueue()
            .sumOf { it.weight ?: 0.0 }

        return totalQueueWeightKg / totalFleetCapacityKg
    }
}
