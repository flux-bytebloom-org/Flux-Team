package org.byte_bloom.flux

import org.byte_bloom.flux.logic.parsers.*
import org.byte_bloom.flux.logic.readers.CsvReader
import org.byte_bloom.flux.logic.sorters.SelectionSort


fun main() {


    val packageLines = CsvReader.read("src/main/resources/packages.csv")
    val cleanPackageLines = CsvParser.cleanLines(packageLines)
    val packages = PackageParser.parse(cleanPackageLines)

    val warehouseLines = CsvReader.read("src/main/resources/warehouses.csv")
    val cleanWarehouseLines = CsvParser.cleanLines(warehouseLines)
    val warehouses = WarehouseParser.parse(cleanWarehouseLines)

    val routeLines = CsvReader.read("src/main/resources/routes.csv")
    val cleanRouteLines = CsvParser.cleanLines(routeLines)
    val routes = RouteParser.parse(cleanRouteLines)

    val fleetLines = CsvReader.read("src/main/resources/fleet.csv")
    val cleanFleetLines = CsvParser.cleanLines(fleetLines)
    val fleet = FleetParser.parse(cleanFleetLines)

    println("--- Parsing Summary ---")
    println("Packages parsed successfully: ${packages.size}")
    println("Warehouses parsed successfully: ${warehouses.size}")
    println("Routes parsed successfully: ${routes.size}")
    println("Fleet parsed successfully: ${fleet.size}")

    val sortedPackages = SelectionSort().sort(packages)

    println("\n--- Top 3 Urgent & Heaviest Packages ---")
    val topPackages = sortedPackages.take(3)
    topPackages.forEach { pkg ->
        println("ID: ${pkg.id}, Weight: ${pkg.weight}, Dest: ${pkg.destinationHubId}, Priority: ${pkg.priority}")
    }

}
