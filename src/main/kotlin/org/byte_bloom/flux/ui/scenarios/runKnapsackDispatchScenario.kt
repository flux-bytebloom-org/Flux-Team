package org.byte_bloom.flux.ui.scenarios

import org.byte_bloom.flux.domain.dispatch.BaseDispatchProcessor
import org.byte_bloom.flux.domain.dispatch.ExpressDispatchProcessor
import org.byte_bloom.flux.domain.dispatch.StandardDispatchProcessor
import org.byte_bloom.flux.domain.dispatch.VehicleCapacityLedger
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.state.Shipment
import org.byte_bloom.flux.domain.usecase.OptimizeCargoWithKnapsackUseCase
import org.koin.java.KoinJavaComponent.getKoin

fun runKnapsackDispatchScenario(packagePool: List<Package>, vehicle: Vehicle) {
    println("\n=== Scenario: Knapsack Cargo Dispatch ===")

    val optimizeCargo: OptimizeCargoWithKnapsackUseCase = getKoin().get()
    val ledger: VehicleCapacityLedger = getKoin().get()
    val standardProcessor: StandardDispatchProcessor = getKoin().get()
    val expressProcessor: ExpressDispatchProcessor = getKoin().get()

    // Optimize: use the capacity still free on the shared ledger, not the max
    val capacity = ledger.remainingKg(vehicle)
    println("Vehicle ${vehicle.id}: capacity=$capacity kg, pool=${packagePool.size} packages")

    val selected = optimizeCargo(packagePool, capacity)
    if (selected.isEmpty()) {
        println("Knapsack selected nothing — nothing to dispatch.")
        return
    }

    val totalWeight = selected.sumOf { it.weight ?: 0.0 }
    println("Selected ${selected.size} packages, total weight=$totalWeight kg:")
    selected.forEach { println("  - ${it.id} | ${it.weight} kg | ${it.priority}") }

    val selectedIds = selected.map { it.id }.toSet()
    val rejected = packagePool.filterNot { it.id in selectedIds }
    println("Rejected ${rejected.size}:")
    rejected.forEach { println("  - ${it.id} | ${it.weight} kg | ${it.priority}") }

    // Dispatch through the Template Method pipeline (Express for URGENT, Standard otherwise)
    val shipments = selected.map { Shipment(it) }
    shipments.forEach { shipment ->
        val processor: BaseDispatchProcessor =
            if (shipment.pkg.priority == Priority.URGENT) expressProcessor else standardProcessor

        print("\n[${shipment.pkg.id}] before: ${shipment.state.name}")
        println(" ==>  after : ${shipment.state.name}")
        processor.process(shipment, vehicle)
    }

    println("\nRemaining capacity on ${vehicle.id}: ${ledger.remainingKg(vehicle)} kg")

    // Finish the lifecycle: InTransit -> Delivered
    println("\n--- Completing deliveries ---")
    shipments.forEach { it.markDelivered() }
    shipments.forEach { println("${it.pkg.id}: ${it.history.joinToString("  |  ")}") }

    // Prove illegal transitions are blocked
    println("\n--- Illegal transition check ---")
    try {
        shipments.first().startTransit() // already Delivered
    } catch (e: LogisticsException.BusinessLogicException.IllegalStateTransitionException) {
        println("Blocked as expected: ${e.message}")
    }

    ledger.release(vehicle)
}
