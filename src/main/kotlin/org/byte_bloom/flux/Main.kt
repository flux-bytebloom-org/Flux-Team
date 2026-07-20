package org.byte_bloom.flux

import org.byte_bloom.flux.utils.parsers.*
import org.byte_bloom.flux.utils.readers.readCsv
import org.byte_bloom.flux.logic.sorters.sortByPriorityAndWeightDescending


fun main() {


    val packageLines = readCsv("src/main/resources/packages.csv")
    val cleanPackageLines = cleanLines(packageLines)
    val packages = parsePackages(cleanPackageLines)

    val warehouseLines = readCsv("src/main/resources/warehouses.csv")
    val cleanWarehouseLines = cleanLines(warehouseLines)
    val warehouses = parseWarehouses(cleanWarehouseLines)

    val routeLines = readCsv("src/main/resources/routes.csv")
    val cleanRouteLines = cleanLines(routeLines)
    val routes = parseRoutes(cleanRouteLines)

    val fleetLines = readCsv("src/main/resources/fleet.csv")
    val cleanFleetLines = cleanLines(fleetLines)
    val fleet = parseVehicles(cleanFleetLines)

    println("--- Parsing Summary ---")
    println("Packages parsed successfully: ${packages.size}")
    println("Warehouses parsed successfully: ${warehouses.size}")
    println("Routes parsed successfully: ${routes.size}")
    println("Fleet parsed successfully: ${fleet.size}")

    val sortedPackages = sortByPriorityAndWeightDescending(packages)

    println("\n--- Top 3 Urgent & Heaviest Packages ---")
    val topPackages = sortedPackages.take(3)
    topPackages.forEach { pkg ->
        println("ID: ${pkg.id}, Weight: ${pkg.weight}, Dest: ${pkg.destinationHubId}, Priority: ${pkg.priority}")
    }


}
