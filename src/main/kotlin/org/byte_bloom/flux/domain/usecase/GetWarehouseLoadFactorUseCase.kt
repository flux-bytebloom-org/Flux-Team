package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.UseCaseException
import org.byte_bloom.flux.domain.model.Warehouse

class GetWarehouseLoadFactorUseCase {

    operator fun invoke(warehouse: Warehouse): Double {

        val totalFleetCapacityKg = warehouse.getStationedVehicles()
            .sumOf { it.maxCapacityKg }

        if (totalFleetCapacityKg <= 0.0) {
            throw UseCaseException.NoStationedVehicles(warehouse.id)
        }

        val totalQueueWeightKg = warehouse.getCargoQueue()
            .sumOf { it.weight ?: 0.0 }

        return totalQueueWeightKg / totalFleetCapacityKg
    }
}
