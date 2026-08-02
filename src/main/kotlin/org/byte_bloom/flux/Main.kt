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

    testPricingEngineMaria()
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
private fun testPricingEngineMaria() {

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
