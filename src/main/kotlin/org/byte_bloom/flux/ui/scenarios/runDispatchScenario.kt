package org.byte_bloom.flux.ui.scenarios

import org.byte_bloom.flux.domain.logic.pricing.EcoStrategy
import org.byte_bloom.flux.domain.logic.pricing.RoutePricingEngine
import org.byte_bloom.flux.domain.logic.routing.DijkstraRouter
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.usecase.AssignPackageToLowestCostStationedVehicleUseCase
import org.byte_bloom.flux.domain.usecase.CalculatePricingUseCase
import org.byte_bloom.flux.domain.usecase.ClassifyTripUrgencyUseCase
import org.byte_bloom.flux.domain.usecase.DecideRoutingWayUseCase
import org.byte_bloom.flux.domain.usecase.DispatchVehicleUseCase
import org.byte_bloom.flux.domain.usecase.FindFastestPathUseCase
import org.byte_bloom.flux.domain.usecase.FindOptimalPathUseCase
import org.byte_bloom.flux.domain.usecase.FindStationedVehiclesByCapacityUseCase

fun runDispatchScenario(
    hub: Warehouse,
    destination: Warehouse,
    pkg: Package,
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

    val price = dispatchUseCases.calculatePricingUseCase(pkg, distanceKm)
    println("Calculated price for ${pkg.id}: $price (distance=$distanceKm km)")

    val vehicle = dispatchUseCases.assignPackageToLowestCostStationedVehicleUseCase(hub, pkg, distanceKm)
    if (vehicle == null) {
        println("No eligible vehicle found at ${hub.id} for package ${pkg.id}.")
        return
    }
    println("Assigned vehicle: ${vehicle.id} (cost/km=${vehicle.costPerKm})")

    val loadedPackages = dispatchUseCases.dispatchVehicleUseCase(hub, vehicle)
    println("Dispatched ${vehicle.id} with ${loadedPackages.size} packages: ${loadedPackages.map { it.id }}")
}

private class DispatchUseCases {
    val classifyTripUrgencyUseCase = ClassifyTripUrgencyUseCase()
    val dijkstraRouter = DijkstraRouter()
    val findFastestPathUseCase = FindFastestPathUseCase(dijkstraRouter)
    val findOptimalPathUseCase = FindOptimalPathUseCase(dijkstraRouter)
    val decideRoutingWayUseCase = DecideRoutingWayUseCase(findFastestPathUseCase, findOptimalPathUseCase)

    val routePricingEngine = RoutePricingEngine(EcoStrategy())
    val calculatePricingUseCase = CalculatePricingUseCase(routePricingEngine)

    val findStationedVehiclesByCapacityUseCase = FindStationedVehiclesByCapacityUseCase()
    val assignPackageToLowestCostStationedVehicleUseCase =
        AssignPackageToLowestCostStationedVehicleUseCase(findStationedVehiclesByCapacityUseCase)

    val dispatchVehicleUseCase = DispatchVehicleUseCase()
}
