package org.byte_bloom.flux.ui.utils

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse


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

