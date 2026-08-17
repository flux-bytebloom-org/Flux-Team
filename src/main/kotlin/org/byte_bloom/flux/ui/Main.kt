package org.byte_bloom.flux.ui

import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.data.dataholders.VehicleRaw
import org.byte_bloom.flux.data.dataholders.WarehouseRaw
import org.byte_bloom.flux.data.repositoryimplementation.CsvPackageRepository
import org.byte_bloom.flux.data.repositoryimplementation.CsvRouteRepository
import org.byte_bloom.flux.data.repositoryimplementation.CsvVehicleRepository
import org.byte_bloom.flux.data.repositoryimplementation.CsvWarehouseRepository
import org.byte_bloom.flux.domain.builder.DomainGraphBuilder
import org.byte_bloom.flux.domain.logic.Decorator.ColdChainDecorator
import org.byte_bloom.flux.domain.logic.Decorator.ExpressInsuranceDecorator
import org.byte_bloom.flux.domain.logic.Decorator.FragileHandlingDecorator
import org.byte_bloom.flux.domain.model.PackageComponent
import org.byte_bloom.flux.domain.logic.routing.BreadthFirstRouter
import org.byte_bloom.flux.domain.logic.routing.DijkstraRouter
import org.byte_bloom.flux.domain.logic.sorting.sortByPriorityAndWeightDescending
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.ui.utils.drowPackageAssignmentRing
import org.byte_bloom.flux.ui.utils.printWarehouseGraph

private const val TOP_PACKAGES_DISPLAY_COUNT = 3
private const val SAMPLE_BASE_TRANSIT_RATE = 100.0

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


    val domainGraphBuilder = DomainGraphBuilder(
        warehouseRepository = warehouseRepository,
        packageRepository = packageRepository,
        routeRepository = routeRepository,
        vehicleRepository = vehicleRepository
    )
    val warehousesGraph = domainGraphBuilder.buildGraph()
    printWarehouseGraph(warehousesGraph)

    testBidirectionalIdentity(warehousesGraph)

    testWarehouseQuickSort(warehousesGraph)

    demonstrateLeastHopRouting(warehousesGraph)

    demonstrateShortestDistanceRouting(warehousesGraph)

    demonstratePackageDecorators(warehousesGraph)

    drowPackageAssignmentRing()

}


private fun printParsingSummary(
    packages: List<PackageRaw>,
    warehouses: List<WarehouseRaw>,
    routes: List<RouteRaw>,
    fleet: List<VehicleRaw>
) {
    println("--- Parsing Summary ---")
    println("Packages parsed successfully: ${packages.size}")
    println("Warehouses parsed successfully: ${warehouses.size}")
    println("Routes parsed successfully: ${routes.size}")
    println("Fleet parsed successfully: ${fleet.size}")
}

private fun printTopPriorityPackages(packages: List<PackageRaw>) {
    val sortedPackages = sortByPriorityAndWeightDescending(packages)

    println("\n--- Top 3 Urgent & Heaviest Packages ---")
    val topPackages = sortedPackages.take(TOP_PACKAGES_DISPLAY_COUNT)
    topPackages.forEach { pkg ->
        printPackageLine(pkg)
    }

}

private fun printPackageLine(pkg: PackageRaw) {
    val id = pkg.id
    val weight = pkg.weight
    val dest = pkg.destinationHubId
    val priority = pkg.priority
    println("ID: $id, Weight: $weight, Dest: $dest, Priority: $priority")
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

private fun demonstrateLeastHopRouting(warehouses: List<Warehouse>) {
    println("\n--- Testing Least-Hop BFS Routing ---")

    if (warehouses.size < 2) {
        println("Need at least 2 warehouses to demonstrate routing.")
        return
    }

    val router = BreadthFirstRouter()
    val start = warehouses.first()
    val destination = warehouses.last()

    val path = router.findLeastHopPath(start, destination)

    if (path.isEmpty()) {
        println("No path found between ${start.name} and ${destination.name}.")
    } else {
        println("Shortest path (${path.size - 1} hops): ${path.joinToString(" -> ") { it.name }}")
    }
}

private fun demonstrateShortestDistanceRouting(warehouses: List<Warehouse>) {
    println("\n--- Testing Optimal Transit Router (Dijkstra) ---")

    if (warehouses.size < 2) {
        println("Need at least 2 warehouses to demonstrate routing.")
        return
    }

    val router = DijkstraRouter()
    val start = warehouses.first()
    val destination = warehouses.last()

    val path = router.findShortestPath(start, destination)

    if (path.isEmpty()) {
        println("No path found between ${start.name} and ${destination.name}.")
    } else {
        val totalDistance = path.zipWithNext().sumOf { (from, to) ->
            from.getOutgoingRoutes().first { it.destinationHub.id == to.id }.distanceKm
        }
        println("Shortest path by distance (${totalDistance}km): ${path.joinToString(" -> ") { it.name }}")
    }
}

private fun demonstratePackageDecorators(warehouses: List<Warehouse>) {
    println("\n--- Testing Package Decorators ---")

    val samplePackage = warehouses
        .firstOrNull { it.getCargoQueue().isNotEmpty() }
        ?.getCargoQueue()
        ?.firstOrNull()

    if (samplePackage == null) {
        println("No packages found to decorate.")
        return
    }

    val fullyDecorated: PackageComponent = ExpressInsuranceDecorator(
        ColdChainDecorator(
            FragileHandlingDecorator(samplePackage)
        )
    )

    val finalRate = fullyDecorated.calculateTransitRate(SAMPLE_BASE_TRANSIT_RATE)
    println("Package ${samplePackage.id}: base rate $SAMPLE_BASE_TRANSIT_RATE -> final rate $finalRate")
}
