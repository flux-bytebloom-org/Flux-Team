package org.byte_bloom.flux.ui

import org.byte_bloom.flux.domain.algorithm.pricing.decorator.ColdChainDecorator
import org.byte_bloom.flux.domain.algorithm.pricing.decorator.ExpressInsuranceDecorator
import org.byte_bloom.flux.domain.algorithm.pricing.decorator.FragileHandlingDecorator
import org.byte_bloom.flux.domain.algorithm.routing.BidirectionalBfsRouter
import org.byte_bloom.flux.domain.algorithm.routing.BreadthFirstRouter
import org.byte_bloom.flux.domain.algorithm.routing.DijkstraRouter
import org.byte_bloom.flux.domain.algorithm.routing.benchmarkRouters
import org.byte_bloom.flux.domain.algorithm.routing.testRoutingComparison
import org.byte_bloom.flux.domain.algorithm.sorting.sortByPriorityAndWeightDescending
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.ui.utils.drowPackageAssignmentRing
import org.byte_bloom.flux.ui.utils.printWarehouseGraph
import org.byte_bloom.flux.di.networkModule
import org.byte_bloom.flux.di.repositoryModule
import org.byte_bloom.flux.di.useCaseModule
import org.byte_bloom.flux.di.validatorModule
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.request.GreedyDispatchRequest
import org.byte_bloom.flux.domain.usecase.DispatchVehicleUseCase
import org.byte_bloom.flux.domain.usecase.GreedyFleetDispatchUseCase
import org.byte_bloom.flux.domain.usecase.crud.warehouse.GetWarehouseByIdUseCase
import org.byte_bloom.flux.ui.scenarios.testPackageCrudFlow
import org.byte_bloom.flux.ui.scenarios.testWarehouseCrudFlow
import org.byte_bloom.flux.ui.utils.runAllScenarios
import org.byte_bloom.flux.ui.utils.testCommandPattern
import org.byte_bloom.flux.ui.utils.testGreedyFleetDispatchUseCase
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.getKoin

private const val TOP_PACKAGES_DISPLAY_COUNT = 3
private const val DEFAULT_BASE_RATE = 100.0


private const val WAREHOUSES_CSV_PATH = "src/main/resources/warehouses.csv"
private const val PACKAGES_CSV_PATH = "src/main/resources/packages.csv"
private const val ROUTES_CSV_PATH = "src/main/resources/routes.csv"
private const val FLEET_CSV_PATH = "src/main/resources/fleet.csv"

fun main() = kotlinx.coroutines.runBlocking {
    startKoin {
        modules(networkModule, repositoryModule, validatorModule, useCaseModule)
    }

    val init = initializeAndPrintGraph()

    val useCase : GetWarehouseByIdUseCase = getKoin().get()
    val hub = useCase("WH-010")
    println(hub)


   testBidirectionalIdentity(init.warehouses)
    testWarehouseQuickSort(init.warehouses)
    drowPackageAssignmentRing()
    testRoutingComparison(init.warehouses)
    testDecoratorStacking(init.warehouses)

    val allRoutes = init.warehouses.flatMap { it.getOutgoingRoutes() }
    benchmarkRouters(init.warehouses)

    testWarehouseCrudFlow()
    testPackageCrudFlow()

    // ===== Sub-Task 5: Greedy Fleet Dispatcher =====
    testGreedyFleetDispatchUseCase()

    val dispatchVehicleUseCase : DispatchVehicleUseCase = getKoin().get()

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

        //comment this part until doing exception handling
        runAllScenarios(init.warehouses, init.packages)
        testCommandPattern()

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
    val warehouseRepository : WarehouseRepository = getKoin().get()
    val routeRepository : RouteRepository = getKoin().get()
    val vehicleRepository : VehicleRepository = getKoin().get()
    val packageRepository : PackageRepository = getKoin().get()

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
