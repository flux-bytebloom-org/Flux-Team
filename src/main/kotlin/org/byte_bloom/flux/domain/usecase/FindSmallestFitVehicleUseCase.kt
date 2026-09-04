package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

class FindSmallestFitVehicleUseCase {

    operator fun invoke(hub: Warehouse, pkg: Package): Vehicle? {
        val packageWeight = pkg.weight ?: 0.0

        return hub.getStationedVehicles()
            .filter { it.maxCapacityKg >= packageWeight }
            .minByOrNull { it.maxCapacityKg }
    }
}
