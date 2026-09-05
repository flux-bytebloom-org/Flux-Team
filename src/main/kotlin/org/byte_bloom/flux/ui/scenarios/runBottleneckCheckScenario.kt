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
import org.byte_bloom.flux.ui.utils.logWarning

private const val DEFAULT_MIN_TRANSIT_LOAD = 1

fun runBottleneckCheckScenario(
    warehouses: List<Warehouse>,
    packages: List<Package>,
    minTransitLoad: Int = DEFAULT_MIN_TRANSIT_LOAD
): BottleneckCheckResult {
    println("\n=== Scenario: Bottleneck Check ===")

    val extractUniqueShipmentRoutesUseCase = ExtractUniqueShipmentRoutesUseCase()
    val dijkstraRouter = DijkstraRouter()
    val findOptimalPathUseCase = FindOptimalPathUseCase(dijkstraRouter)
    val generateWeightedShipmentPathsUseCase = GenerateWeightedShipmentPathsUseCase(findOptimalPathUseCase)
    val findBottleneckWarehousesUseCase = FindBottleneckWarehousesUseCase()
    val shadowWarehouseGraphBuilder = ShadowWarehouseGraphBuilder()
    val penalizeBottleneckRoutesUseCase = PenalizeBottleneckRoutesUseCase(shadowWarehouseGraphBuilder)
    val filterActualAlternativePathsUseCase = FilterActualAlternativePathsUseCase()
    val getWarehouseLoadFactorUseCase = GetWarehouseLoadFactorUseCase()
    val calculateRebalanceRatioUseCase = CalculateRebalanceRatioUseCase()
    val reroutePackageUseCase = ReroutePackageUseCase()
    val splitAndRerouteShipmentsUseCase = SplitAndRerouteShipmentsUseCase(reroutePackageUseCase)

    val warehousesById = warehouses.associateBy { it.id.uppercase() }
    val uniqueRoutes = extractUniqueShipmentRoutesUseCase(packages)

    val weightedPaths = generateWeightedShipmentPathsUseCase(uniqueRoutes, warehousesById)

    val bottlenecks = findBottleneckWarehousesUseCase(weightedPaths, minTransitLoad)
    if (bottlenecks.isEmpty()) {
        return BottleneckCheckResult(packages, null, 0, 0, emptyList())
    }

    val topBottleneck = bottlenecks.first().warehouse
    val shadowWarehousesById = penalizeBottleneckRoutesUseCase(topBottleneck, warehouses)

    val alternativePaths = weightedPaths.associate { weightedPath ->
        val originId = weightedPath.path.first().id
        val destinationId = weightedPath.path.last().id
        val shadowOrigin = shadowWarehousesById[originId]
        val shadowDestination = shadowWarehousesById[destinationId]
        val altPath = if (shadowOrigin != null && shadowDestination != null) {
            findOptimalPathUseCase(shadowOrigin, shadowDestination)
        } else emptyList()
        (originId to destinationId) to altPath
    }

    val actualAlternatives = filterActualAlternativePathsUseCase(weightedPaths, topBottleneck, alternativePaths)
    if (actualAlternatives.isEmpty()) {
        return BottleneckCheckResult(packages, topBottleneck.id, 0, 0, emptyList())
    }

    var currentPackages = packages
    var currentRoutePlans: Map<String, List<Warehouse>> = emptyMap()
    val summaries = mutableListOf<RebalanceSummaryEntry>()

    actualAlternatives.forEach { (originalPath, newPath) ->
        val bottleneckLoadFactor = safeLoadFactor(topBottleneck, getWarehouseLoadFactorUseCase)

        val intermediateStops = newPath.drop(1).dropLast(1)
            .mapNotNull { shadowStop -> warehousesById[shadowStop.id.uppercase()] }
        val loadFactors = intermediateStops.map { safeLoadFactor(it, getWarehouseLoadFactorUseCase) }
        val alternativeLoadFactor = if (loadFactors.isEmpty()) 0.0 else loadFactors.average()

        val ratio = calculateRebalanceRatioUseCase(bottleneckLoadFactor, alternativeLoadFactor)

        val beforeCount = currentRoutePlans.size
        val result = splitAndRerouteShipmentsUseCase(originalPath, newPath, ratio, currentPackages, currentRoutePlans)
        currentPackages = result.reroutedPackages
        currentRoutePlans = result.updatedRoutePlans
        val movedThisRound = currentRoutePlans.size - beforeCount

        if (movedThisRound > 0) {
            summaries.add(
                RebalanceSummaryEntry(
                    originId = originalPath.path.first().id,
                    destinationId = originalPath.path.last().id,
                    packagesRerouted = movedThisRound,
                    oldPath = originalPath.path.map { it.id },
                    newPath = newPath.map { it.id },
                    oldDistanceKm = pathDistance(originalPath.path),
                    newDistanceKm = pathDistance(newPath)
                )
            )
        }
    }

    return BottleneckCheckResult(
        finalPackages = currentPackages,
        bottleneckWarehouseId = topBottleneck.id,
        totalPackagesConsidered = weightedPaths.sumOf { it.packageCount },
        totalPackagesRerouted = summaries.sumOf { it.packagesRerouted },
        rebalanceSummaries = summaries
    )
}

private fun pathDistance(path: List<Warehouse>): Double {
    return path.zipWithNext().sumOf { (a, b) ->
        a.getOutgoingRoutes().firstOrNull { it.destinationHub.id == b.id }?.distanceKm ?: 0.0
    }
}

private fun safeLoadFactor(warehouse: Warehouse, useCase: GetWarehouseLoadFactorUseCase): Double {
    return try { useCase(warehouse) } catch (e: UseCaseException.NoStationedVehicles) { 0.0 }
}


