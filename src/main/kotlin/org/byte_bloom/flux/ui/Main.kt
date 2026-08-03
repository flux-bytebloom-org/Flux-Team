package org.byte_bloom.flux.ui

import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.data.dataholders.VehicleRaw
import org.byte_bloom.flux.data.dataholders.WarehouseRaw
import org.byte_bloom.flux.data.loaders.loadFleet
import org.byte_bloom.flux.data.loaders.loadPackages
import org.byte_bloom.flux.data.loaders.loadRoutes
import org.byte_bloom.flux.data.loaders.loadWarehouses
import org.byte_bloom.flux.domain.builder.DomainGraphBuilder
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.logic.sorting.sortByPriorityAndWeightDescending
import org.byte_bloom.flux.ui.utils.printWarehouseGraph

private const val TOP_PACKAGES_DISPLAY_COUNT = 3


fun main() {


    val packages = loadPackages()
    val warehouses = loadWarehouses()
    val routes = loadRoutes()
    val fleet = loadFleet()
    printParsingSummary(packages, warehouses, routes, fleet)
    printTopPriorityPackages(packages)


    val warehousesGraph = DomainGraphBuilder().buildGraph(warehouses, packages, routes, fleet)
    printWarehouseGraph(warehousesGraph)

    testBidirectionalIdentity(warehousesGraph)

    testWarehouseQuickSort(warehousesGraph)

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

