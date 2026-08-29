package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

class FindStationedVehiclesByCapacityUseCase {
    operator fun invoke(warehouse: Warehouse, requiredWeight: Double): List<Vehicle> {

        require(requiredWeight >= 0) {
            "Required weight cannot be negative"
        }

        return warehouse.getStationedVehicles()
            .filter { it.maxCapacityKg >= requiredWeight }
    }
}
