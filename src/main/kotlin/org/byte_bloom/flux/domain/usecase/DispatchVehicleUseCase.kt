package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

class DispatchVehicleUseCase {

    operator fun invoke(hub: Warehouse, vehicle: Vehicle): List<Package> {
        val packages = hub.getCargoQueue()
        var currentWeight = 0.0

        return packages.filter { pkg ->
            val weight = pkg.weight ?: 0.0
            if (currentWeight + weight <= vehicle.maxCapacityKg) {
                currentWeight += weight
                true
            } else {
                false
            }
        }
    }
}
