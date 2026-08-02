package org.byte_bloom.flux

import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseFleet
import org.byte_bloom.flux.data.parsers.parsePackages
import org.byte_bloom.flux.data.parsers.parseRoutes
import org.byte_bloom.flux.data.parsers.parseWarehouses
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.operations.pricing.EcoStrategy
import org.byte_bloom.flux.domain.operations.pricing.ExpressStrategy
import org.byte_bloom.flux.domain.operations.pricing.RoutePricingEngine
import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.data.dataholders.VehicleRaw
import org.byte_bloom.flux.data.dataholders.WarehouseRaw
import org.byte_bloom.flux.domain.builder.DomainGraphBuilder
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.operations.sorting.sortByPriorityAndWeightDescending

private const val TOP_PACKAGES_DISPLAY_COUNT = 3
private const val TEST_TRANSIT_DISTANCE = 100.0
private const val TEST_TRANSIT_WEIGHT = 50.0

fun main() {


    val packages = loadPackages()
    val warehouses = loadWarehouses()
    val routes = loadRoutes()
    val fleet = loadFleet()
    printParsingSummary(packages, warehouses, routes, fleet)
    printTopPriorityPackages(packages)


    val warehousesGraph = DomainGraphBuilder().buildGraph(warehouses, packages, routes, fleet)
    printWarehouseGraph(warehousesGraph)

    //testBidirectionalIdentity(warehousesGraph)

    //testWarehouseQuickSort(warehousesGraph)

    //testPricingEngine()
}


private fun loadPackages(): List<PackageRaw> {
    val lines = readCsv("src/main/resources/packages.csv")
    val cleanLines = cleanLines(lines)
    return parsePackages(cleanLines)
}

private fun loadWarehouses(): List<WarehouseRaw> {
    val lines = readCsv("src/main/resources/warehouses.csv")
    val cleanLines = cleanLines(lines)
    return parseWarehouses(cleanLines)
}

private fun loadRoutes(): List<RouteRaw> {
    val routeLines = readCsv("src/main/resources/routes.csv")
    val cleanRouteLines = cleanLines(routeLines)
    return parseRoutes(cleanRouteLines)
}

private fun loadFleet(): List<VehicleRaw> {
    val fleetLines = readCsv("src/main/resources/fleet.csv")
    val cleanFleetLines = cleanLines(fleetLines)
    return parseFleet(cleanFleetLines)
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

/* For Maria Testing */
private fun testPricingEngine() {
    println("\n--- Testing Pricing Engine ---")
    val engine = RoutePricingEngine(
        EcoStrategy()
    )

    println(
        "Eco cost: " +
                engine.calculateTransitCost(
                    TEST_TRANSIT_DISTANCE,
                    TEST_TRANSIT_WEIGHT
                )
    )

    engine.changeStrategy(
        ExpressStrategy()
    )

    println(
        "Express cost: " +
                engine.calculateTransitCost(
                    TEST_TRANSIT_DISTANCE,
                    TEST_TRANSIT_WEIGHT
                )
    )

}

private fun testBidirectionalIdentity(warehouses: List<Warehouse>) {
    println("\n--- Testing Bidirectional Reference Identity ---")

    val warehouse = warehouses.firstOrNull { it.getStationedVehicles().isNotEmpty() }

    if (warehouse == null) {
        println("No warehouse with vehicles found to test.")
        return
    }

    val vehicle = warehouse.getStationedVehicles().first()

    // فحص identity حقيقي: نفس عنوان الذاكرة، مو مجرد == (equals محتوى)
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



fun printWarehouseGraph(warehouses: List<Warehouse>) {
    println("\n--- Warehouse Network Graph ---")

    warehouses.forEach { warehouse ->
        println("${warehouse.name} (${warehouse.id})")

        printBranch(
            label = "cargoQueue",
            items = warehouse.getCargoQueue().map(::formatPackage),
            isLast = false
        )
        printBranch(
            label = "outgoingRoutes",
            items = warehouse.getOutgoingRoutes().map(::formatRoute),
            isLast = false
        )
        printBranch(
            label = "stationedVehicles",
            items = warehouse.getStationedVehicles().map(::formatVehicle),
            isLast = true
        )

        println()
    }
}

private fun printBranch(label: String, items: List<String>, isLast: Boolean) {
    val branchConnector = if (isLast) "└──" else "├──"
    println(" $branchConnector $label(${items.size})")

    val childPrefix = if (isLast) "     " else " │   "

    items.forEachIndexed { index, item ->
        val itemConnector = if (index == items.lastIndex) "└──" else "├──"
        println("$childPrefix$itemConnector $item")
    }
}

private fun formatPackage(pkg: Package): String {
    val weightLabel = pkg.weight?.let { "${it}kg" } ?: "unknown weight"
    return "${pkg.id} ($weightLabel, ${pkg.priority}) → ${pkg.destinationHub.name}"
}

private fun formatRoute(route: Route): String {
    return "${route.id} → ${route.destinationHub.name} (${route.distanceKm}km, delay ${route.typicalDelayMin}min)"
}

private fun formatVehicle(vehicle: Vehicle): String {
    return "${vehicle.id} (cap: ${vehicle.maxCapacityKg}kg, cost/km: ${vehicle.costPerKm})"
}
