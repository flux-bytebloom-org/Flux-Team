package org.byte_bloom.flux.ui

import org.byte_bloom.flux.data.csv.datasource.CsvPackageDataSource
import org.byte_bloom.flux.data.csv.datasource.CsvRouteDataSource
import org.byte_bloom.flux.data.csv.datasource.CsvVehicleDataSource
import org.byte_bloom.flux.data.csv.datasource.CsvWarehouseDataSource
import org.byte_bloom.flux.data.remote.datasource.impl.SupabasePackageDataSource
import org.byte_bloom.flux.data.remote.datasource.impl.SupabaseWarehouseDataSource
import org.byte_bloom.flux.data.repositoryimplementation.PackageRepositoryImpl
import org.byte_bloom.flux.data.repositoryimplementation.RouteRepositoryImpl
import org.byte_bloom.flux.data.repositoryimplementation.VehicleRepositoryImpl
import org.byte_bloom.flux.data.repositoryimplementation.WarehouseRepositoryImpl
import org.byte_bloom.flux.domain.logic.pricing.decorator.ColdChainDecorator
import org.byte_bloom.flux.domain.logic.pricing.decorator.ExpressInsuranceDecorator
import org.byte_bloom.flux.domain.logic.pricing.decorator.FragileHandlingDecorator
import org.byte_bloom.flux.domain.logic.routing.BidirectionalBfsRouter
import org.byte_bloom.flux.domain.logic.routing.BreadthFirstRouter
import org.byte_bloom.flux.domain.logic.routing.DijkstraRouter
import org.byte_bloom.flux.domain.logic.routing.benchmarkRouters
import org.byte_bloom.flux.domain.logic.routing.testRoutingComparison
import org.byte_bloom.flux.domain.logic.sorting.sortByPriorityAndWeightDescending
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.usecase.FindFewestHopsRouteUseCase
import org.byte_bloom.flux.domain.usecase.FindOptimalPathUseCase
import org.byte_bloom.flux.ui.utils.drowPackageAssignmentRing
import org.byte_bloom.flux.ui.utils.printWarehouseGraph
import org.byte_bloom.flux.data.remote.datasource.impl.SupabaseRouteDataSource
import org.byte_bloom.flux.data.remote.datasource.impl.SupabaseVehicleDataSource
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.usecase.DispatchVehicleUseCase
import org.byte_bloom.flux.domain.usecase.GreedyDispatchRequest
import org.byte_bloom.flux.domain.usecase.GreedyFleetDispatchUseCase
import org.byte_bloom.flux.ui.scenarios.testPackageCrudFlow
import org.byte_bloom.flux.ui.scenarios.testWarehouseCrudFlow
import org.byte_bloom.flux.ui.utils.testGreedyFleetDispatchUseCase

private const val TOP_PACKAGES_DISPLAY_COUNT = 3
private const val DEFAULT_BASE_RATE = 100.0


private const val WAREHOUSES_CSV_PATH = "src/main/resources/warehouses.csv"
private const val PACKAGES_CSV_PATH = "src/main/resources/packages.csv"
private const val ROUTES_CSV_PATH = "src/main/resources/routes.csv"
private const val FLEET_CSV_PATH = "src/main/resources/fleet.csv"

fun main() = kotlinx.coroutines.runBlocking {
    val init = initializeAndPrintGraph()

        testBidirectionalIdentity(init.warehouses)
        testWarehouseQuickSort(init.warehouses)
        drowPackageAssignmentRing()

        val bfsRouter = BreadthFirstRouter()
        val dijkstraRouter = DijkstraRouter()
        val findOptimalPathUseCase = FindOptimalPathUseCase(dijkstraRouter)
        val findFewestHopsRouteUseCase = FindFewestHopsRouteUseCase(bfsRouter)
        testRoutingComparison(init.warehouses, findFewestHopsRouteUseCase, findOptimalPathUseCase)
        testDecoratorStacking(init.warehouses)

        val allRoutes = init.warehouses.flatMap { it.getOutgoingRoutes() }
        val bidirectionalRouter = BidirectionalBfsRouter(allRoutes)
        benchmarkRouters(init.warehouses, bfsRouter, bidirectionalRouter)

    testWarehouseCrudFlow(init.warehouseRepository)
    testPackageCrudFlow(init.packageRepository, init.warehouseRepository)

    // ===== Sub-Task 5: Greedy Fleet Dispatcher =====
    testGreedyFleetDispatchUseCase()

    val dispatchVehicleUseCase = DispatchVehicleUseCase(init.packageRepository, findOptimalPathUseCase)

    val dispatchedVehicles = init.vehicleRepository.getAll().map { vehicle ->
        dispatchVehicleUseCase(vehicle.currentHub, vehicle)
    }

    val targetZones = init.warehouses
        .map { it.regionalZone }
        .filter { it != RegionalZone.UnKNOWN }
        .toSet()

    val greedyResult = GreedyFleetDispatchUseCase()(GreedyDispatchRequest(targetZones, dispatchedVehicles))

    println("\n--- Sub-Task 5: Greedy Fleet Dispatcher ---")
    println("Target zones: $targetZones")
    println("Selected vehicles: ${greedyResult.selectedVehicles.map { it.id }}")
    println("Uncovered zones: ${greedyResult.uncoveredZones}")

        /*comment this part until doing exception handling
        runAllScenarios(
            init.warehouses, init.packages,
            init.vehicleRepository, init.warehouseRepository, init.packageRepository
        )
        testCommandPattern(init.vehicleRepository, init.warehouseRepository, init.packageRepository)
        */

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

private suspend fun initializeAndPrintGraph(): InitResult {
    val warehouseRepository = WarehouseRepositoryImpl(
        localDataSource = CsvWarehouseDataSource(WAREHOUSES_CSV_PATH),
        remoteDataSource = SupabaseWarehouseDataSource()
    )
    val routeRepository = RouteRepositoryImpl(
        localRouteDataSource = CsvRouteDataSource(ROUTES_CSV_PATH),
        remoteRouteDataSource = SupabaseRouteDataSource(),
        warehouseRepository = warehouseRepository
    )
    val vehicleRepository = VehicleRepositoryImpl(
        localDataSource = CsvVehicleDataSource(FLEET_CSV_PATH),
        remoteDataSource = SupabaseVehicleDataSource(),
        warehouseRepository=warehouseRepository
    )
    val packageRepository = PackageRepositoryImpl(
        pkgDataSource = CsvPackageDataSource(PACKAGES_CSV_PATH),
        remoteDataSource = SupabasePackageDataSource(),
        warehouseRepository = warehouseRepository
    )
    val packages = packageRepository.getAll()
    val warehouses = warehouseRepository.getAll()
    val routes = routeRepository.getAll()
    val fleet = vehicleRepository.getAll()

    printParsingSummary(packages, warehouses, routes, fleet)
    printTopPriorityPackages(packages)
    printWarehouseGraph(warehouses)

    return InitResult(warehouses, packages, vehicleRepository, warehouseRepository, packageRepository, routeRepository)
}

private data class InitResult(
    val warehouses: List<Warehouse>,
    val packages: List<Package>,
    val vehicleRepository: VehicleRepository,
    val warehouseRepository: WarehouseRepository,
    val packageRepository: PackageRepository,
    val routeRepository: RouteRepository
)
