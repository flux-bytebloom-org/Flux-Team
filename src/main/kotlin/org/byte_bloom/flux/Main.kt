package org.byte_bloom.flux

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle

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
        printPackageLine(pkg)
    }
    // saraTest()
}

private fun printPackageLine(pkg: Package) {
    val id = pkg.packageId
    val weight = pkg.weight
    val dest = pkg.destinationHubId
    val priority = pkg.priority
    println("ID: $id, Weight: $weight, Dest: $dest, Priority: $priority")
}
