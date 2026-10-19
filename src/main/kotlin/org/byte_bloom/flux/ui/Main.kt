package org.byte_bloom.flux.ui

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.byte_bloom.flux.domain.algorithm.pricing.decorator.ColdChainDecorator
import org.byte_bloom.flux.domain.algorithm.pricing.decorator.ExpressInsuranceDecorator
import org.byte_bloom.flux.domain.algorithm.pricing.decorator.FragileHandlingDecorator
import org.byte_bloom.flux.domain.algorithm.sorting.sortByPriorityAndWeightDescending
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.ui.utils.printWarehouseGraph
import org.byte_bloom.flux.di.networkModule
import org.byte_bloom.flux.di.repositoryModule
import org.byte_bloom.flux.di.useCaseModule
import org.byte_bloom.flux.di.validatorModule
import org.byte_bloom.flux.domain.algorithm.routing.testRoutingComparison
import org.byte_bloom.flux.domain.algorithm.routing.benchmarkRouters
import org.byte_bloom.flux.domain.algorithm.routing.testRoutingComparison
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.request.GreedyDispatchRequest
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.request.PackageUpdateRequest
import org.byte_bloom.flux.domain.request.RouteUpdateRequest
import org.byte_bloom.flux.domain.request.VehicleUpdateRequest
import org.byte_bloom.flux.domain.request.WarehouseUpdateRequest
import org.byte_bloom.flux.domain.usecase.DispatchVehicleUseCase
import org.byte_bloom.flux.domain.usecase.GreedyFleetDispatchUseCase
import org.byte_bloom.flux.domain.usecase.crud.pakage.CreatePackageUseCase
import org.byte_bloom.flux.domain.usecase.crud.pakage.DeletePackageUseCase
import org.byte_bloom.flux.domain.usecase.crud.pakage.GetPackageByIdUseCase
import org.byte_bloom.flux.domain.usecase.crud.pakage.UpdatePackageUseCase
import org.byte_bloom.flux.domain.usecase.crud.route.CreateRouteUseCase
import org.byte_bloom.flux.domain.usecase.crud.route.DeleteRouteUseCase
import org.byte_bloom.flux.domain.usecase.crud.route.GetRouteByIdUseCase
import org.byte_bloom.flux.domain.usecase.crud.route.UpdateRouteUseCase
import org.byte_bloom.flux.domain.usecase.crud.vehicle.CreateVehicleUseCase
import org.byte_bloom.flux.domain.usecase.crud.vehicle.DeleteVehicleUseCase
import org.byte_bloom.flux.domain.usecase.crud.vehicle.GetVehicleByIdUseCase
import org.byte_bloom.flux.domain.usecase.crud.vehicle.UpdateVehicleUseCase
import org.byte_bloom.flux.domain.usecase.crud.warehouse.CreateWarehouseUseCase
import org.byte_bloom.flux.domain.usecase.crud.warehouse.DeleteWarehouseUseCase
import org.byte_bloom.flux.domain.usecase.crud.warehouse.GetWarehouseByIdUseCase
import org.byte_bloom.flux.domain.usecase.crud.warehouse.UpdateWarehouseUseCase
import org.byte_bloom.flux.ui.scenarios.runKnapsackDispatchScenario
import org.byte_bloom.flux.ui.scenarios.runTelemetryScenario
import org.byte_bloom.flux.ui.scenarios.testPackageCrudFlow
import org.byte_bloom.flux.ui.scenarios.testWarehouseCrudFlow
import org.byte_bloom.flux.ui.utils.drowPackageAssignmentRing
import org.byte_bloom.flux.ui.utils.runAllScenarios
import org.byte_bloom.flux.ui.utils.testCommandPattern
import org.byte_bloom.flux.ui.utils.NetworkLoader
import org.byte_bloom.flux.ui.utils.drowPackageAssignmentRing
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

    val networkLoader : NetworkLoader = getKoin().get()
    val network = networkLoader.load()

    printParsingSummary(network.packages, network.warehouses, network.routes, network.fleet)
    printTopPriorityPackages(network.packages)
    printWarehouseGraph(network.warehouses)

    testCrudOperations(network.warehouses)


       testBidirectionalIdentity(network.warehouses)
        testWarehouseQuickSort(network.warehouses)
        drowPackageAssignmentRing()
        testRoutingComparison(network.warehouses)
        testDecoratorStacking(network.warehouses)
        runTelemetryScenario(network.fleet)

        val allRoutes = network.warehouses.flatMap { it.getOutgoingRoutes() }
        //benchmarkRouters(init.warehouses)

        testWarehouseCrudFlow()
        testPackageCrudFlow()

        // ===== Sub-Task 5: Greedy Fleet Dispatcher =====
        testGreedyFleetDispatchUseCase()

        val dispatchVehicleUseCase : DispatchVehicleUseCase = getKoin().get()

        val dispatchedVehicles = network.fleet.map { vehicle ->
            dispatchVehicleUseCase(vehicle.currentHub, vehicle)
        }

        val targetZones = network.warehouses
            .map { it.regionalZone }
            .filter { it != RegionalZone.UnKNOWN }
            .toSet()

        val greedyResult = GreedyFleetDispatchUseCase()(GreedyDispatchRequest(targetZones, dispatchedVehicles))

        println("\n--- Sub-Task 5: Greedy Fleet Dispatcher ---")
        println("Target zones: $targetZones")
        println("Selected vehicles: ${greedyResult.selectedVehicles.map { it.id }}")
        println("Uncovered zones: ${greedyResult.uncoveredZones}")
    
        //comment this part until doing exception handling
        //runAllScenarios(init.warehouses, init.packages)
        //testCommandPattern()

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

private suspend fun testCrudOperations(warehouses: List<Warehouse>) {
    println("\n--- Testing Warehouse CRUD Flow ---")
    val getWarehouseUseCase : GetWarehouseByIdUseCase = getKoin().get()
    val gotHub = getWarehouseUseCase("WH-010")
    println(gotHub)

    val createWarehouseUseCase : CreateWarehouseUseCase = getKoin().get()
    val hubToCreate = Warehouse("WH-ZZZ", "Warehouse ZZ", RegionalZone.NORTH,1.5,16.1)
    val createdHub = createWarehouseUseCase(hubToCreate)
    println(createdHub)

    val updateWarehouseUseCase : UpdateWarehouseUseCase = getKoin().get()
    val hub3re = WarehouseUpdateRequest( "Warehouse ZZ Updated", "NORTH",20.5,16.1)
    val updatedHub = updateWarehouseUseCase("WH-ZZZ", hub3re)
    println(updatedHub)

    val deleteWarehouseUseCase : DeleteWarehouseUseCase = getKoin().get()
    val deletedHub = deleteWarehouseUseCase("WH-ZZZ")
    println(deletedHub)


    println("\n--- Testing Package CRUD Flow ---")
    val getPackageUseCase : GetPackageByIdUseCase = getKoin().get()
    val gotPackage = getPackageUseCase("PKG-009993")
    println(gotPackage)

    val createPackageUseCase : CreatePackageUseCase = getKoin().get()
    val packageToCreate = Package("PKG-ZZZ", 10.0, warehouses[0], warehouses[1], Priority.STANDARD)
    val createdPackage = createPackageUseCase(packageToCreate)
    println(createdPackage)

    val updatePackageUseCase : UpdatePackageUseCase = getKoin().get()
    val package3re = PackageUpdateRequest( 15.0, "W-ZZZ", "W-AAA", "URGENT")
    val updatedPackage = updatePackageUseCase("PKG-ZZZ", package3re)
    println(updatedPackage)

    val deletePackageUseCase : DeletePackageUseCase = getKoin().get()
    val deletedPackage = deletePackageUseCase("PKG-ZZZ")
    println(deletedPackage)

    println("\n--- Testing Vehicle CRUD Flow ---")
    val getVehicleUseCase : GetVehicleByIdUseCase = getKoin().get()
    val gotVehicle = getVehicleUseCase("TRK-0005")
    println(gotVehicle)

    val createVehicleUseCase : CreateVehicleUseCase = getKoin().get()
    val vehicleToCreate = Vehicle("TRK-ZZZ", warehouses[0], 100.0, 50.0)
    val createdVehicle = createVehicleUseCase(vehicleToCreate)
    println(createdVehicle)

    val updateVehicleUseCase : UpdateVehicleUseCase = getKoin().get()
    val vehicle3re = VehicleUpdateRequest(warehouses[5].id,120.0, 60.0)
    val updatedVehicle = updateVehicleUseCase("TRK-ZZZ", vehicle3re)
    println(updatedVehicle)

    val deleteVehicleUseCase : DeleteVehicleUseCase = getKoin().get()
    val deletedVehicle = deleteVehicleUseCase("TRK-ZZZ")
    println(deletedVehicle)


    println("\n--- Testing Route CRUD Flow ---")
    val getRouteUseCase : GetRouteByIdUseCase = getKoin().get()
    val gotRoute = getRouteUseCase("RT-01496")
    println(gotRoute)

    val createRouteUseCase : CreateRouteUseCase = getKoin().get()
    val routeToCreate = Route("RT-ZZZ", warehouses[0],warehouses[1], 10.0, 5.0)
    val createdRoute = createRouteUseCase(routeToCreate)
    println(createdRoute)

    val updateRouteUseCase : UpdateRouteUseCase = getKoin().get()
    val route3re = RouteUpdateRequest(warehouses[1].id, warehouses[0].id, 15.0, 7.0)
    val updatedRoute = updateRouteUseCase("RT-ZZZ", route3re)
    println(updatedRoute)

    val deleteRouteUseCase : DeleteRouteUseCase = getKoin().get()
    val deletedRoute = deleteRouteUseCase("RT-ZZZ")
    println(deletedRoute)

}
