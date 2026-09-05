package org.byte_bloom.flux.ui.scenarios

import org.byte_bloom.flux.domain.logic.routing.BreadthFirstRouter
import org.byte_bloom.flux.domain.logic.tree.buildWarehouseTree
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.usecase.AddVehicleToHubUseCase
import org.byte_bloom.flux.domain.usecase.AssignPackageToCargoQueueUseCase
import org.byte_bloom.flux.domain.usecase.FindFewestHopsRouteUseCase
import org.byte_bloom.flux.domain.usecase.FindSmallestFitVehicleUseCase
import org.byte_bloom.flux.domain.usecase.TraceHubLineageUseCase

private const val TEST_VEHICLE_CAPACITY = 500.0
private const val TEST_VEHICLE_COST_PER_KM = 3.0
private const val TEST_PACKAGE_WEIGHT = 15.0

fun runStandaloneUseCaseDemos(warehouses: List<Warehouse>) {
    println("\n=== Standalone Use Case Demos ===")

    testAddVehicleToHub(warehouses)
    testAssignPackageToCargoQueue(warehouses)
    testFindOptimalVehicleForPackage(warehouses)
    testFindFewestHopsRoute(warehouses)
    testTraceHubLineage(warehouses)
}

// 1) AddVehicleToHubUseCase — uses a real warehouse, guaranteed not to affect other scenarios
private fun testAddVehicleToHub(warehouses: List<Warehouse>) {
    println("\n[Standalone] AddVehicleToHubUseCase")

    val hub = warehouses.firstOrNull() ?: run {
        println("[SKIP] No warehouse available.")
        return
    }

    val addVehicleToHubUseCase = AddVehicleToHubUseCase()
    val beforeCount = hub.getStationedVehicles().size

    val newVehicle = Vehicle(
        id = "TEST-V-${System.currentTimeMillis()}",
        currentHub = hub,
        maxCapacityKg = TEST_VEHICLE_CAPACITY,
        costPerKm = TEST_VEHICLE_COST_PER_KM
    )

    addVehicleToHubUseCase(hub, newVehicle)

    val afterCount = hub.getStationedVehicles().size
    val status = if (afterCount == beforeCount + 1) "[PASS]" else "[FAIL]"
    println("$status Vehicle added to ${hub.id}: before=$beforeCount after=$afterCount")
}

// 2) AssignPackageToCargoQueueUseCase — verifies that addition + sorting happen correctly
private fun testAssignPackageToCargoQueue(warehouses: List<Warehouse>) {
    println("\n[Standalone] AssignPackageToCargoQueueUseCase")

    val hub = warehouses.firstOrNull() ?: run {
        println("[SKIP] No warehouse available.")
        return
    }

    val assignPackageToCargoQueueUseCase = AssignPackageToCargoQueueUseCase()
    val beforeCount = hub.getCargoQueue().size

    val newPackage = Package(
        id = "TEST-P-${System.currentTimeMillis()}",
        weight = TEST_PACKAGE_WEIGHT,
        originHub = hub,
        destinationHub = hub,
        priority = Priority.STANDARD
    )

    assignPackageToCargoQueueUseCase(hub, newPackage)

    val afterCount = hub.getCargoQueue().size
    val isSorted = hub.getCargoQueue()
        .zipWithNext()
        .all { (a, b) -> (a.weight ?: -1.0) >= (b.weight ?: -1.0) }

    val status = if (afterCount == beforeCount + 1 && isSorted) "[PASS]" else "[FAIL]"
    println("$status Package added to ${hub.id}: before=$beforeCount after=$afterCount sorted=$isSorted")
}

// 3) FindOptimalVehicleForPackageUseCase — "best-fit by capacity" decision, independent of Dispatch scenario
private fun testFindOptimalVehicleForPackage(warehouses: List<Warehouse>) {
    println("\n[Standalone] FindOptimalVehicleForPackageUseCase")

    val hub = warehouses.firstOrNull {
        it.getStationedVehicles().isNotEmpty() && it.getCargoQueue().isNotEmpty()
    }

    if (hub == null) {
        println("[SKIP] No warehouse with both vehicles and packages found.")
        return
    }

    val findSmallestFitVehicleUseCase = FindSmallestFitVehicleUseCase()
    val pkg = hub.getCargoQueue().first()

    val bestFitVehicle = findSmallestFitVehicleUseCase(hub, pkg)

    println("Package: ${pkg.id} (${pkg.weight}kg) at ${hub.id}")
    println("  best-fit vehicle -> ${bestFitVehicle?.id ?: "none"} (cap=${bestFitVehicle?.maxCapacityKg})")
}

// 4) FindFewestHopsRouteUseCase — BFS, completely separate from Dijkstra used in scenarios
private fun testFindFewestHopsRoute(warehouses: List<Warehouse>) {
    println("\n[Standalone] FindFewestHopsRouteUseCase")

    if (warehouses.size < 2) {
        println("[SKIP] Not enough warehouses to test routing.")
        return
    }

    val findFewestHopsRouteUseCase = FindFewestHopsRouteUseCase(BreadthFirstRouter())
    val start = warehouses.first()
    val destination = warehouses.last()

    val result = findFewestHopsRouteUseCase(start, destination)

    if (result.path.isEmpty()) {
        println("[INFO] No path found between ${start.id} and ${destination.id}.")
    } else {
        println(
            "[PASS] Fewest-hops path ${start.id} -> ${destination.id}: " +
                    "${result.path.map { it.id }} (${result.path.size - 1} hops, ${result.nodesExplored} explored)"
        )
    }
}

// 5) TraceHubLineageUseCase — works on WarehouseTree, not on the regular graph
private fun testTraceHubLineage(warehouses: List<Warehouse>) {
    println("\n[Standalone] TraceHubLineageUseCase")

    val globalHub = warehouses.firstOrNull()
    val leafWarehouse = warehouses.lastOrNull { it.id != globalHub?.id }

    when {
        globalHub == null -> {
            println("[SKIP] No warehouse available to act as global hub.")
        }
        leafWarehouse == null -> {
            println("[SKIP] No leaf warehouse found to trace.")
        }
        else -> {
            val warehouseTree = buildWarehouseTree(warehouses, globalHub)
            val traceHubLineageUseCase = TraceHubLineageUseCase()
            val leafNode = warehouseTree.findNode(leafWarehouse.id)

            if (leafNode == null) {
                println("[FAIL] Could not find node for ${leafWarehouse.id} in the tree.")
            } else {
                val lineage = traceHubLineageUseCase(leafNode)
                println("[PASS] Lineage of ${leafWarehouse.id}: ${lineage.map { it.id }}")
            }
        }
    }
}
