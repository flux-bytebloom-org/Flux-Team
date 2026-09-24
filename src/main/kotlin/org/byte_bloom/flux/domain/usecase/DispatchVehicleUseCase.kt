package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.response.DispatchedVehicle
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository

class DispatchVehicleUseCase(
    private val packageRepo: PackageRepository,
    private val findOptimalPath: FindOptimalPathUseCase
) {

    operator fun invoke(hub: Warehouse, vehicle: Vehicle): DispatchedVehicle {
        val queue = hub.getCargoQueue()

        val targetHub = selectTargetHub(queue)
            ?: return DispatchedVehicle(vehicle = vehicle, loadedPackages = emptyList(), totalWeight = 0.0,emptyList())

        val pathWarehouses = findOptimalPath(start = hub, destination = targetHub)
        val allowedHubIds = pathWarehouses.map { it.id }.toSet()

        val eligiblePackages = queue.filter { it.destinationHub.id in allowedHubIds }

        val prioritized = eligiblePackages.sortedByDescending { it.priority == Priority.URGENT }

        val selectedPackages = selectPackagesWithinCapacity(prioritized, vehicle.maxCapacityKg)

        val totalWeight = selectedPackages.fold(0.0) { acc, pkg -> acc + (pkg.weight ?: 0.0) }

        selectedPackages.forEach { pkg ->
            packageRepo.removePackageFromHub(pkg, hub)
        }

        return DispatchedVehicle(
            vehicle = vehicle,
            loadedPackages = selectedPackages,
            totalWeight = totalWeight,
            path = pathWarehouses
        )
    }

    private fun selectTargetHub(queue: List<Package>): Warehouse? {
        val urgentGrouped = queue
            .filter { it.priority == Priority.URGENT }
            .groupBy { it.destinationHub }

        val candidates = urgentGrouped.ifEmpty {
            queue.groupBy { it.destinationHub }
        }

        return candidates.maxWithOrNull(
            compareBy(
                { it.value.size },
                { it.value.fold(0.0) { acc, pkg -> acc + (pkg.weight ?: 0.0) } }
            )
        )?.key
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
