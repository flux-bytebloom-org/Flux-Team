package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.response.DispatchedVehicle

class DispatchVehicleUseCase (
    val packageRepo: PackageRepository
){

    operator fun invoke(hub: Warehouse, vehicle: Vehicle): DispatchedVehicle {
        val selectedPackages = selectPackagesWithinCapacity(hub.getCargoQueue(),vehicle.maxCapacityKg)

        selectedPackages.forEach { pkg ->
            packageRepo.removePackageFromHub(pkg, hub)
        }

        val totalWeight = selectedPackages.fold(0.0) { acc, pkg -> acc + (pkg.weight ?: 0.0) }

        return DispatchedVehicle(
            vehicle = vehicle,
            loadedPackages = selectedPackages,
            totalWeight = totalWeight
        )
    }

    private fun selectPackagesWithinCapacity(
        packages: List<Package>,
        maxCapacity: Double
    ): List<Package> {
        return packages.fold(Pair(emptyList<Package>(), 0.0)) { (accepted, weight), pkg ->
            val pkgWeight = pkg.weight ?: 0.0
            if (weight + pkgWeight <= maxCapacity) {
                Pair(accepted + pkg, weight + pkgWeight)
            } else {
                Pair(accepted, weight)
            }
        }.first
    }
}
