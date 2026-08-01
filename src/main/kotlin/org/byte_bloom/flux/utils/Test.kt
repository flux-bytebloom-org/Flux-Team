package org.byte_bloom.flux.utils

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.model.Priority


fun saraTest() {
    printSaraTestHeader()

    val mockWarehouse = buildMockWarehouseWithCargo()

    println("\n--- [BEFORE SORTING - Original Order] ---")
    printCargoQueue(mockWarehouse)

    println("\n[Running mockWarehouse.sortCargoQueue()...]")
    mockWarehouse.sortCargoQueue()

    println("\n--- [AFTER SORTING - Descending By Weight] ---")
    printCargoQueue(mockWarehouse)

    printSaraTestFooter()
}

private fun printSaraTestHeader() {
    println("==================================================")
    println("   TESTING WAREHOUSE CARGO QUICKSORT (TASK 5)    ")
    println("==================================================")
}

private fun printSaraTestFooter() {
    println("\n==================================================")
    println("             TEST PASSED SUCCESSFULLY!            ")
    println("==================================================")
}

private fun buildMockWarehouseWithCargo(): Warehouse {
    val mockWarehouse = Warehouse(
        warehouseId = "WH-GAZA-01",
        warehouseName = "Central Distribution Hub",
        regionalZone = "Zone-1"
    )

    mockWarehouse.cargoQueue.addAll(createMockPackagesGroupOne())
    mockWarehouse.cargoQueue.addAll(createMockPackagesGroupTwo())

    return mockWarehouse
}

private fun createMockPackagesGroupOne(): List<Package> {
    return listOf(
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
        )
    )
}

private fun createMockPackagesGroupTwo(): List<Package> {
    return listOf(
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
}

private fun printCargoQueue(warehouse: Warehouse) {
    warehouse.cargoQueue.forEachIndexed { index, pkg ->
        printCargoLine(index, pkg)
    }
}

private fun printCargoLine(index: Int, pkg: Package) {
    val position = index + 1
    val weightLabel = pkg.weight ?: "0.0 (null)"
    println("$position. ID: ${pkg.packageId} | Weight: $weightLabel kg | Priority: ${pkg.priority}")
}
