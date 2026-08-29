package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

class DispatchVehicleUseCase {

    operator fun invoke(hub: Warehouse, vehicle: Vehicle): List<Package> {
        val packages = hub.getCargoQueue()

        val cumulativeWeights = packages
            .map { it.weight ?: 0.0 }
            .runningFold(0.0) { sum, weight -> sum + weight }
            .drop(1)

        return packages.zip(cumulativeWeights)
            .takeWhile { (_, totalWeight) -> totalWeight <= vehicle.maxCapacityKg }
            .map { (pkg, _) -> pkg }
    }
}

