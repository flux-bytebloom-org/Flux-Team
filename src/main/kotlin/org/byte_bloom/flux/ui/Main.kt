package org.byte_bloom.flux.ui

import org.byte_bloom.flux.data.repositoryimplementation.CsvPackageRepository
import org.byte_bloom.flux.data.repositoryimplementation.CsvRouteRepository
import org.byte_bloom.flux.data.repositoryimplementation.CsvVehicleRepository
import org.byte_bloom.flux.data.repositoryimplementation.CsvWarehouseRepository
import org.byte_bloom.flux.domain.builder.DomainGraphBuilder
import org.byte_bloom.flux.domain.logic.pricing.decorator.ColdChainDecorator
import org.byte_bloom.flux.domain.logic.pricing.decorator.ExpressInsuranceDecorator
import org.byte_bloom.flux.domain.logic.pricing.decorator.FragileHandlingDecorator
import org.byte_bloom.flux.domain.logic.routing.BidirectionalBfsRouter
import org.byte_bloom.flux.domain.logic.routing.BreadthFirstRouter
import org.byte_bloom.flux.domain.logic.routing.DijkstraRouter
import org.byte_bloom.flux.domain.logic.routing.FakeBidirectionalRouter
import org.byte_bloom.flux.domain.logic.routing.benchmarkRouters
import org.byte_bloom.flux.domain.logic.routing.testRoutingComparison
import org.byte_bloom.flux.domain.logic.sorting.sortByPriorityAndWeightDescending
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.usecase.FindFewestHopsRouteUseCase
import org.byte_bloom.flux.domain.usecase.FindOptimalPathUseCase
import org.byte_bloom.flux.ui.utils.drowPackageAssignmentRing
import org.byte_bloom.flux.ui.utils.printWarehouseGraph

private const val TOP_PACKAGES_DISPLAY_COUNT = 3
private const val DEFAULT_BASE_RATE = 100.0


private const val WAREHOUSES_CSV_PATH = "src/main/resources/warehouses.csv"
private const val PACKAGES_CSV_PATH = "src/main/resources/packages.csv"
private const val ROUTES_CSV_PATH = "src/main/resources/routes.csv"
private const val FLEET_CSV_PATH = "src/main/resources/fleet.csv"

fun main() {

    val warehouseRepository = CsvWarehouseRepository(WAREHOUSES_CSV_PATH)
    val packageRepository = CsvPackageRepository(PACKAGES_CSV_PATH)
    val routeRepository = CsvRouteRepository(ROUTES_CSV_PATH)
    val vehicleRepository = CsvVehicleRepository(FLEET_CSV_PATH)

    val packages = packageRepository.getAll()
    val warehouses = warehouseRepository.getAll()
    val routes = routeRepository.getAll()
    val fleet = vehicleRepository.getAll()

    printParsingSummary(packages, warehouses, routes, fleet)
    printTopPriorityPackages(packages)

    val domainGraphBuilder = DomainGraphBuilder(warehouseRepository = warehouseRepository,
        packageRepository = packageRepository,routeRepository = routeRepository,vehicleRepository = vehicleRepository)
    val warehousesGraph = domainGraphBuilder.buildGraph()
    printWarehouseGraph(warehousesGraph)
    testBidirectionalIdentity(warehousesGraph)
    testWarehouseQuickSort(warehousesGraph)
    drowPackageAssignmentRing()

    val bfsRouter = BreadthFirstRouter()
    val dijkstraRouter = DijkstraRouter()
    val findOptimalPathUseCase = FindOptimalPathUseCase(dijkstraRouter)
    val findFewestHopsRouteUseCase = FindFewestHopsRouteUseCase(bfsRouter)
    testRoutingComparison(warehousesGraph, findFewestHopsRouteUseCase, findOptimalPathUseCase)
    testDecoratorStacking(warehousesGraph)

    val allRoutes = warehousesGraph.flatMap { it.getOutgoingRoutes() }
    val bidirectionalRouter = BidirectionalBfsRouter(allRoutes)
    benchmarkRouters(warehousesGraph, bfsRouter, bidirectionalRouter)

}

private fun printParsingSummary(
    packages: List<Package>,
    warehouses: List<Warehouse>,
    routes: List<Route>,
    fleet: List<Vehicle>
) {
    println("--- Parsing Summary ---")
    println("Packages parsed successfully: ${packages.size}")
    println("Warehouses parsed successfully: ${warehouses.size}")
    println("Routes parsed successfully: ${routes.size}")
    println("Fleet parsed successfully: ${fleet.size}")
}

private fun printTopPriorityPackages(packages: List<Package>) {
    val sortedPackages = sortByPriorityAndWeightDescending(packages)

    println("\n--- Top 3 Urgent & Heaviest Packages ---")
    val topPackages = sortedPackages.take(TOP_PACKAGES_DISPLAY_COUNT)
    topPackages.forEach { pkg ->
        printPackageLine(pkg)
    }

}

private fun printPackageLine(pkg: Package) {
    val id = pkg.id
    val weight = pkg.weight
    val destination = pkg.destinationHub.id
    val priority = pkg.priority
    println("ID: $id, Weight: $weight, Destination: $destination, Priority: $priority")
}

private fun testBidirectionalIdentity(warehouses: List<Warehouse>) {
    println("\n--- Testing Bidirectional Reference Identity ---")

    val warehouse = warehouses.firstOrNull { it.getStationedVehicles().isNotEmpty() }

    if (warehouse == null) {
        println("No warehouse with vehicles found to test.")
        return
    }

    val vehicle = warehouse.getStationedVehicles().first()

    val isSameReference = warehouse === vehicle.currentHub

    println("Warehouse: ${warehouse.name} (${System.identityHashCode(warehouse)})")
    println("Vehicle's currentHub: ${vehicle.currentHub.name} (${System.identityHashCode(vehicle.currentHub)})")
    println("Same heap reference? $isSameReference")

    check(isSameReference) { "Bidirectional reference broken! Not the same object." }
}

private fun testWarehouseQuickSort(warehouses: List<Warehouse>) {
    println("\n--- Testing Warehouse Cargo QuickSort ---")

    val warehouse = warehouses.firstOrNull { it.getCargoQueue().isNotEmpty() }
    if (warehouse == null) {
        println("No warehouse with packages found to test.")
        return
    }

    println("Warehouse: ${warehouse.name}")
    println("Before sorting: ${warehouse.getCargoQueue().map { it.id to it.weight }}")

    warehouse.sortCargoQueue()

    println("After sorting:  ${warehouse.getCargoQueue().map { it.id to it.weight }}")
}



private fun testDecoratorStacking(warehouses: List<Warehouse>) {
    println("\n--- Testing Decorator Stacking ---")

    val pkg = warehouses.firstOrNull { it.getCargoQueue().isNotEmpty() }
        ?.getCargoQueue()?.firstOrNull()

    if (pkg == null) {
        println("No package found to test decorators.")
        return
    }

    val baseRate = DEFAULT_BASE_RATE

    println("Base: ${pkg.getDescription()} → ${pkg.calculateTransitRate(baseRate)}")

    val fragile = FragileHandlingDecorator(pkg)
    println("+ Fragile: ${fragile.getDescription()} → ${fragile.calculateTransitRate(baseRate)}")

    val fragileAndCold = ColdChainDecorator(fragile)
    println("+ ColdChain: ${fragileAndCold.getDescription()} → ${fragileAndCold.calculateTransitRate(baseRate)}")

    val fullyStacked = ExpressInsuranceDecorator(fragileAndCold)
    println("+ ExpressInsurance: ${fullyStacked.getDescription()} → ${fullyStacked.calculateTransitRate(baseRate)}")
}
