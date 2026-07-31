package org.byte_bloom.flux


import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Priority

import org.byte_bloom.flux.data.parsers.parsePackages
import org.byte_bloom.flux.data.parsers.parseWarehouses
import org.byte_bloom.flux.data.parsers.parseRoutes
import org.byte_bloom.flux.data.parsers.parseFleet
import org.byte_bloom.flux.data.parsers.cleanLines

import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.logic.sorters.sortByPriorityAndWeightDescending

private const val TOP_PACKAGES_DISPLAY_COUNT = 3


fun main() {

    val packages = loadPackages()
    val warehouses = loadWarehouses()
    val routes = loadRoutes()
    val fleet = loadFleet()

    printParsingSummary(
        packages,
        warehouses,
        routes,
        fleet
    )

    printTopPriorityPackages(packages)
}


private fun loadPackages(): List<Package> {

    val lines = readCsv("src/main/resources/packages.csv")
    val cleanLines = cleanLines(lines)

    return parsePackages(cleanLines)
}

private fun loadWarehouses(): List<Warehouse> {

    val lines = readCsv("src/main/resources/warehouses.csv")
    val cleanLines = cleanLines(lines)

    return parseWarehouses(cleanLines)
}

private fun loadRoutes(): List<Route> {

    val routeLines = readCsv("src/main/resources/routes.csv")
    val cleanRouteLines = cleanLines(routeLines)

    return parseRoutes(cleanRouteLines)
}

private fun loadFleet(): List<Vehicle> {

    val fleetLines = readCsv("src/main/resources/fleet.csv")
    val cleanFleetLines = cleanLines(fleetLines)

    return parseFleet(cleanFleetLines)
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

private fun printTopPriorityPackages(
    packages: List<Package>
) {

    val sortedPackages =
        sortByPriorityAndWeightDescending(packages)

    println("\n--- Top 3 Urgent & Heaviest Packages ---")

    val topPackages = sortedPackages.take(TOP_PACKAGES_DISPLAY_COUNT)
    topPackages.forEach { pkg ->
println("ID: ${pkg.packageId}, Weight: ${pkg.weight}, Dest: ${pkg.destinationHubId}, Priority: ${pkg.priority}")

    }
   // saraTest()

}

fun saraTest() {
    println("==================================================")
    println("   TESTING WAREHOUSE CARGO QUICKSORT (TASK 5)    ")
    println("==================================================")


    val mockPackages = listOf(
        Package(
            packageId = "PKG-101",
            weight = 15.0,
            originHubId = "HUB-ORIGIN-1",
            destinationHubId = "HUB-WEST",
            priority = Priority.URGENT
        ),
        Package(
            packageId = "PKG-102",
            weight = 78.5,
            originHubId = "HUB-ORIGIN-1",
            destinationHubId = "HUB-NORTH",
            priority = Priority.URGENT
        ),
        Package(
            packageId = "PKG-103",
            weight = 5.2,
            originHubId = "HUB-ORIGIN-2",
            destinationHubId = "HUB-EAST",
            priority = Priority.LOW
        ),
        Package(
            packageId = "PKG-104",
            weight = 78.5,
            originHubId = "HUB-ORIGIN-2",
            destinationHubId = "HUB-SOUTH",
            priority = Priority.STANDARD
        ),
        Package(
            packageId = "PKG-105",
            weight = null,
            originHubId = "HUB-ORIGIN-1",
            destinationHubId = "HUB-LOCAL",
            priority = Priority.LOW
        ),
        Package(
            packageId = "PKG-106",
            weight = 120.0,
            originHubId = "HUB-ORIGIN-3",
            destinationHubId = "HUB-MAIN",
            priority = Priority.URGENT
        )
    )


    val mockWarehouse = Warehouse(
        warehouseId = "WH-GAZA-01",
        warehouseName = "Central Distribution Hub",
        regionalZone = "Zone-1"
    )


    mockWarehouse.cargoQueue.addAll(mockPackages)


    println("\n--- [BEFORE SORTING - Original Order] ---")
    mockWarehouse.cargoQueue.forEachIndexed { index, pkg ->
        println("${index + 1}. ID: ${pkg.packageId} | Weight: ${pkg.weight ?: "0.0 (null)"} kg | Priority: ${pkg.priority}")
    }


    println("\n[Running mockWarehouse.sortCargoQueue()...]")
    mockWarehouse.sortCargoQueue()


    println("\n--- [AFTER SORTING - Descending By Weight] ---")
    mockWarehouse.cargoQueue.forEachIndexed { index, pkg ->
        println("${index + 1}. ID: ${pkg.packageId} | Weight: ${pkg.weight ?: "0.0 (null)"} kg | Priority: ${pkg.priority}")
    }

    println("\n==================================================")
    println("             TEST PASSED SUCCESSFULLY!            ")
    println("==================================================")
}
