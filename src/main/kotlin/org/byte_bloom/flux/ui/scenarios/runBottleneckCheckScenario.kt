package org.byte_bloom.flux.ui.scenarios

import org.byte_bloom.flux.domain.builder.ShadowWarehouseGraphBuilder
import org.byte_bloom.flux.domain.exception.UseCaseException
import org.byte_bloom.flux.domain.logic.routing.DijkstraRouter
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.BottleneckCheckResult
import org.byte_bloom.flux.domain.response.RebalanceSummaryEntry
import org.byte_bloom.flux.domain.response.WeightedPath
import org.byte_bloom.flux.domain.usecase.CalculateRebalanceRatioUseCase
import org.byte_bloom.flux.domain.usecase.ExtractUniqueShipmentRoutesUseCase
import org.byte_bloom.flux.domain.usecase.FilterActualAlternativePathsUseCase
import org.byte_bloom.flux.domain.usecase.FindBottleneckWarehousesUseCase
import org.byte_bloom.flux.domain.usecase.FindOptimalPathUseCase
import org.byte_bloom.flux.domain.usecase.GenerateWeightedShipmentPathsUseCase
import org.byte_bloom.flux.domain.usecase.GetWarehouseLoadFactorUseCase
import org.byte_bloom.flux.domain.usecase.PenalizeBottleneckRoutesUseCase
import org.byte_bloom.flux.domain.usecase.ReroutePackageUseCase
import org.byte_bloom.flux.domain.usecase.SplitAndRerouteShipmentsUseCase

private const val DEFAULT_MIN_TRANSIT_LOAD = 1

private class BottleneckUseCases {
    val extractUniqueShipmentRoutesUseCase = ExtractUniqueShipmentRoutesUseCase()
    val findOptimalPathUseCase = FindOptimalPathUseCase(DijkstraRouter())
    val generateWeightedShipmentPathsUseCase = GenerateWeightedShipmentPathsUseCase(findOptimalPathUseCase)
    val findBottleneckWarehousesUseCase = FindBottleneckWarehousesUseCase()
    val penalizeBottleneckRoutesUseCase = PenalizeBottleneckRoutesUseCase(ShadowWarehouseGraphBuilder())
    val filterActualAlternativePathsUseCase = FilterActualAlternativePathsUseCase()
    val getWarehouseLoadFactorUseCase = GetWarehouseLoadFactorUseCase()
    val calculateRebalanceRatioUseCase = CalculateRebalanceRatioUseCase()
    val splitAndRerouteShipmentsUseCase = SplitAndRerouteShipmentsUseCase(ReroutePackageUseCase())
}

fun runBottleneckCheckScenario(
    warehouses: List<Warehouse>,
    packages: List<Package>,
    minTransitLoad: Int = DEFAULT_MIN_TRANSIT_LOAD
): BottleneckCheckResult {

    println("\n=== Scenario: Bottleneck Check ===")
    val useCases = BottleneckUseCases()
    val warehousesById = warehouses.associateBy { it.id.uppercase() }

    val weightedPaths = computeWeightedPaths(packages, warehousesById, useCases)
    val topBottleneck = findTopBottleneck(weightedPaths, minTransitLoad, useCases)

    return when {
        topBottleneck == null -> {
            BottleneckCheckResult(packages, null, 0, 0, emptyList())
        }
        else -> {
            val actualAlternatives = findActualAlternatives(weightedPaths, topBottleneck, warehouses, useCases)
            if (actualAlternatives.isEmpty()) {
                BottleneckCheckResult(packages, topBottleneck.id, 0, 0, emptyList())
            } else {
                executeRebalancing(packages, actualAlternatives, topBottleneck, warehousesById, useCases)
            }
        }
    }
}


private fun computeWeightedPaths(
    packages: List<Package>,
    warehousesById: Map<String, Warehouse>,
    useCases: BottleneckUseCases
): List<WeightedPath> {
    val uniqueRoutes = useCases.extractUniqueShipmentRoutesUseCase(packages)
    return useCases.generateWeightedShipmentPathsUseCase(uniqueRoutes, warehousesById)
}

private fun findTopBottleneck(
    weightedPaths: List<WeightedPath>,
    minTransitLoad: Int,
    useCases: BottleneckUseCases
): Warehouse? {
    val bottlenecks = useCases.findBottleneckWarehousesUseCase(weightedPaths, minTransitLoad)
    return bottlenecks.firstOrNull()?.warehouse
}

private fun findActualAlternatives(
    weightedPaths: List<WeightedPath>,
    topBottleneck: Warehouse,
    warehouses: List<Warehouse>,
    useCases: BottleneckUseCases
): List<Pair<WeightedPath, List<Warehouse>>> {
    val shadowWarehousesById = useCases.penalizeBottleneckRoutesUseCase(topBottleneck, warehouses)

    val alternativePaths = weightedPaths.associate { weightedPath ->
        val originId = weightedPath.path.first().id
        val destinationId = weightedPath.path.last().id
        val shadowOrigin = shadowWarehousesById[originId]
        val shadowDestination = shadowWarehousesById[destinationId]
        val altPath = if (shadowOrigin != null && shadowDestination != null) {
            useCases.findOptimalPathUseCase(shadowOrigin, shadowDestination)
        } else emptyList()
        (originId to destinationId) to altPath
    }

    return useCases.filterActualAlternativePathsUseCase(weightedPaths, topBottleneck, alternativePaths)
}

private fun executeRebalancing(
    originalPackages: List<Package>,
    actualAlternatives: List<Pair<WeightedPath, List<Warehouse>>>,
    topBottleneck: Warehouse,
    warehousesById: Map<String, Warehouse>,
    useCases: BottleneckUseCases
): BottleneckCheckResult {
    var currentPackages = originalPackages
    var currentRoutePlans: Map<String, List<Warehouse>> = emptyMap()
    val summaries = mutableListOf<RebalanceSummaryEntry>()

    actualAlternatives.forEach { (originalPath, newPath) ->
        val ratio = computeRebalanceRatio(topBottleneck, newPath, warehousesById, useCases)

        val beforeCount = currentRoutePlans.size
        val result = useCases.splitAndRerouteShipmentsUseCase(
            originalPath, newPath, ratio, currentPackages, currentRoutePlans
        )
        currentPackages = result.reroutedPackages
        currentRoutePlans = result.updatedRoutePlans

        val movedThisRound = currentRoutePlans.size - beforeCount
        if (movedThisRound > 0) {
            summaries.add(buildSummaryEntry(originalPath, newPath, movedThisRound))
        }
    }

    val totalConsidered = actualAlternatives.sumOf { it.first.packageCount }
    return BottleneckCheckResult(
        finalPackages = currentPackages,
        bottleneckWarehouseId = topBottleneck.id,
        totalPackagesConsidered = totalConsidered,
        totalPackagesRerouted = summaries.sumOf { it.packagesRerouted },
        rebalanceSummaries = summaries
    )
}

private fun computeRebalanceRatio(
    topBottleneck: Warehouse,
    newPath: List<Warehouse>,
    warehousesById: Map<String, Warehouse>,
    useCases: BottleneckUseCases
): Double {
    val bottleneckLoadFactor = safeLoadFactor(topBottleneck, useCases.getWarehouseLoadFactorUseCase)

    val intermediateStops = newPath.drop(1).dropLast(1)
        .mapNotNull { shadowStop -> warehousesById[shadowStop.id.uppercase()] }
    val loadFactors = intermediateStops.map { safeLoadFactor(it, useCases.getWarehouseLoadFactorUseCase) }
    val alternativeLoadFactor = if (loadFactors.isEmpty()) 0.0 else loadFactors.average()

    return useCases.calculateRebalanceRatioUseCase(bottleneckLoadFactor, alternativeLoadFactor)
}

private fun buildSummaryEntry(
    originalPath: WeightedPath,
    newPath: List<Warehouse>,
    movedCount: Int
): RebalanceSummaryEntry {
    return RebalanceSummaryEntry(
        originId = originalPath.path.first().id,
        destinationId = originalPath.path.last().id,
        packagesRerouted = movedCount,
        oldPath = originalPath.path.map { it.id },
        newPath = newPath.map { it.id },
        oldDistanceKm = pathDistance(originalPath.path),
        newDistanceKm = pathDistance(newPath)
    )
}

private fun pathDistance(path: List<Warehouse>): Double {
    return path.zipWithNext().sumOf { (a, b) ->
        a.getOutgoingRoutes().firstOrNull { it.destinationHub.id == b.id }?.distanceKm ?: 0.0
    }
}


private fun safeLoadFactor(warehouse: Warehouse, useCase: GetWarehouseLoadFactorUseCase): Double {
    return try {
        useCase(warehouse)
    } catch (e: UseCaseException.NoStationedVehicles) {
        println("Info: Warehouse ${warehouse.id} has no vehicles: ${e.message}")
        0.0
    }
}
