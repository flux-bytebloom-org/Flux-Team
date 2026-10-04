package org.byte_bloom.flux.ui.scenarios

import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.usecase.AssignPackageToLowestCostStationedVehicleUseCase
import org.byte_bloom.flux.domain.usecase.CalculatePricingUseCase
import org.byte_bloom.flux.domain.usecase.ClassifyTripUrgencyUseCase
import org.byte_bloom.flux.domain.usecase.DecideRoutingWayUseCase
import org.byte_bloom.flux.domain.usecase.DispatchVehicleUseCase
import org.koin.java.KoinJavaComponent.getKoin

fun runDispatchScenario(
    hub: Warehouse,
    destination: Warehouse,
    tripPackages: List<Package>
) {
    println("\n=== Scenario: Dispatch & Pricing ===")

    val dispatchUseCases = DispatchUseCases()


    val isUrgent = dispatchUseCases.classifyTripUrgencyUseCase(tripPackages)
    println("Trip urgency: $isUrgent (based on ${tripPackages.size} packages)")

    val path = dispatchUseCases.decideRoutingWayUseCase(hub, destination, isUrgent)
    if (path.isEmpty()) {
        println("No path found from ${hub.id} to ${destination.id} — aborting dispatch.")
        return
    }
    println("Chosen path: ${path.map { it.id }}")

    val distanceKm = path.zipWithNext().sumOf { (a, b) ->
        a.getOutgoingRoutes().first { it.destinationHub.id == b.id }.distanceKm
    }

    val firstBkg = hub.getCargoQueue().first()

    val price = dispatchUseCases.calculatePricingUseCase(firstBkg, distanceKm)
    println("Calculated price for ${firstBkg.id}: $price (distance=$distanceKm km)")

    val vehicle = dispatchUseCases.assignPackageToLowestCostStationedVehicleUseCase(hub, firstBkg, distanceKm)
    if (vehicle == null) {
        println("No eligible vehicle found at ${hub.id} for package ${firstBkg.id}.")
        return
    }
    println("Assigned vehicle: ${vehicle.id} (cost/km=${vehicle.costPerKm})")

    val loadedPackages = dispatchUseCases.dispatchVehicleUseCase(hub, vehicle).loadedPackages
    println("Dispatched ${vehicle.id} with ${loadedPackages.size} packages: ${loadedPackages.map { it.id }}")
}

private class DispatchUseCases() {
    val classifyTripUrgencyUseCase : ClassifyTripUrgencyUseCase = getKoin().get()

    val decideRoutingWayUseCase : DecideRoutingWayUseCase = getKoin().get()

    val calculatePricingUseCase : CalculatePricingUseCase = getKoin().get()

    val assignPackageToLowestCostStationedVehicleUseCase :
            AssignPackageToLowestCostStationedVehicleUseCase = getKoin().get()

    val dispatchVehicleUseCase : DispatchVehicleUseCase = getKoin().get()
}
