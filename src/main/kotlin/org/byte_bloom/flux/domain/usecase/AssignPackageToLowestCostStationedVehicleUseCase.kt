package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.InvalidPackageWeightException
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

class AssignPackageToLowestCostStationedVehicleUseCase
    (private val findStationedVehiclesByCapacityUseCase: FindStationedVehiclesByCapacityUseCase) {

    operator fun invoke(warehouse: Warehouse, packageItem: Package, distanceKm: Double): Vehicle? {

        val requiredWeight = packageItem.weight
            ?: throw InvalidPackageWeightException(
                "Package weight cannot be null"
            )

        val eligibleVehicles =
            findStationedVehiclesByCapacityUseCase(
                warehouse,
                requiredWeight
            )

        return eligibleVehicles.minByOrNull { vehicle ->
            distanceKm * vehicle.costPerKm
        }
    }
}
