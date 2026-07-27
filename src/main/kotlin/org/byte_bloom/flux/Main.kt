package org.byte_bloom.flux

import org.byte_bloom.flux.data.dataholders.*
import org.byte_bloom.flux.data.parsers.*
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.logic.sorters.sortByPriorityAndWeightDescending

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

    val topPackages = sortedPackages.take(3)
    topPackages.forEach { pkg ->
        println("ID: ${pkg.packageId}, Weight: ${pkg.weight}, Dest: ${pkg.destinationHubId}, Priority: ${pkg.priority}")
    }
}