package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

class FindStationedVehiclesByCapacityUseCase {

    operator fun invoke(hub: Warehouse, requiredCapacityKg: Double): List<Vehicle> {
        return hub.getStationedVehicles()
            .filter { vehicle -> vehicle.maxCapacityKg >= requiredCapacityKg }
    }
}
