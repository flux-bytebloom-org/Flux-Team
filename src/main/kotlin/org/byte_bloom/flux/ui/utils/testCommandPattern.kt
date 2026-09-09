package org.byte_bloom.flux.ui.utils

import org.byte_bloom.flux.domain.logic.command.AddVehicleToHubCommand
import org.byte_bloom.flux.domain.logic.command.AssignPackageToQueueCommand
import org.byte_bloom.flux.domain.logic.command.CommandInvoker
import org.byte_bloom.flux.domain.logic.command.DispatchVehicleCommand
import org.byte_bloom.flux.domain.logic.command.ReroutePackageCommand
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.usecase.AddVehicleToHubUseCase
import org.byte_bloom.flux.domain.usecase.AssignPackageToCargoQueueUseCase
import org.byte_bloom.flux.domain.usecase.DispatchVehicleUseCase
import org.byte_bloom.flux.domain.usecase.ReroutePackageUseCase

private const val DEFAULT_LATITUDE = 0.0
private const val DEFAULT_LONGITUDE = 0.0
private const val VEHICLE_CAPACITY_KG = 30.0
private const val VEHICLE_COST_PER_KM = 5.0
private const val FIRST_PACKAGE_WEIGHT_KG = 10.0
private const val SECOND_PACKAGE_WEIGHT_KG = 20.0
private const val THIRD_PACKAGE_WEIGHT_KG = 12.0

private const val STEPS_TO_UNDO = 2
private const val STEPS_TO_REDO = 1
private const val EXCESSIVE_UNDO_STEPS = 10

private data class ScenarioContext(
    val hubA: Warehouse,
    val hubB: Warehouse,
    val vehicle: Vehicle,
    val invoker: CommandInvoker,
    val assignUseCase: AssignPackageToCargoQueueUseCase,
    val addVehicleUseCase: AddVehicleToHubUseCase,
    val rerouteUseCase: ReroutePackageUseCase,
    val dispatchUseCase: DispatchVehicleUseCase,
    val p1: Package,
    val p2: Package,
    val p3: Package
)

/**
 * End-to-end scenario for the Command Pattern Dispatch Panel (Sub-Task 5 + Bonus Task 2).
 *
 * Story:
 *  1. Three packages are assigned to hubA's queue.
 *  2. A vehicle is moved from hubA to hubB.
 *  3. One package is rerouted from hubA to hubB.
 *  4. undoSteps(2) reverts the last two operations (reroute + vehicle move).
 *  5. redoSteps(1) reapplies only the vehicle move.
 *  6. A new command (dispatch) is executed, which must clear the redoStack
 *     (History Clearance requirement) — verified by a failed redo() right after.
 *  7. undoSteps(10) is requested with more steps than actually available,
 *     confirming it stops safely at the real stack size instead of crashing.
 *  8. Final check confirms the domain state is back to its original empty state,
 *     proving undo/redo stayed consistent through the whole journey.
 */
fun testCommandPattern() {
    println("\n--- Week 5 - Subtask 5 & Bonus Task 2 - Testing Command Pattern Dispatch Panel ---")
    val context = buildScenarioContext()
    runCommandTestScenario(context)
}

private fun buildScenarioContext(): ScenarioContext {
    val hubA = Warehouse("H1", "Main Hub", "ZoneA", DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    val hubB = Warehouse("H2", "Second Hub", "ZoneB", DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    val vehicle = Vehicle("V1", hubA, VEHICLE_CAPACITY_KG, VEHICLE_COST_PER_KM)

    return ScenarioContext(
        hubA = hubA,
        hubB = hubB,
        vehicle = vehicle,
        invoker = CommandInvoker(),
        assignUseCase = AssignPackageToCargoQueueUseCase(),
        addVehicleUseCase = AddVehicleToHubUseCase(),
        rerouteUseCase = ReroutePackageUseCase(),
        dispatchUseCase = DispatchVehicleUseCase(),
        p1 = Package("P1", FIRST_PACKAGE_WEIGHT_KG, hubA, hubA, Priority.URGENT),
        p2 = Package("P2", SECOND_PACKAGE_WEIGHT_KG, hubA, hubA, Priority.STANDARD),
        p3 = Package("P3", THIRD_PACKAGE_WEIGHT_KG, hubA, hubA, Priority.LOW)
    )
}

private fun runCommandTestScenario(context: ScenarioContext) = with(context) {

    println("\nStep 1: Assign P1, P2, P3 to hubA queue")
    assignThreePackagesToHubA(context)

    println("\nStep 2: Move vehicle V1 from hubA to hubB")
    invoker.executeCommand(AddVehicleToHubCommand(hubB, vehicle, addVehicleUseCase))
    printState(context, "after vehicle move")

    println("\nStep 3: Reroute P3 to hubB")
    invoker.executeCommand(ReroutePackageCommand(hubA, p3, hubB, rerouteUseCase))
    printState(context, "after reroute")

    println("\nStep 4: undoSteps($STEPS_TO_UNDO) — undo reroute and vehicle move")
    val undone = invoker.undoSteps(STEPS_TO_UNDO)
    println("  performed=$undone")
    printState(context, "after undoSteps($STEPS_TO_UNDO)")

    println("\nStep 5: redoSteps($STEPS_TO_REDO) — redo only the vehicle move")
    val redoneOnce = invoker.redoSteps(STEPS_TO_REDO)
    println("  performed=$redoneOnce")
    printState(context, "after redoSteps($STEPS_TO_REDO)")

    println("\nStep 6: Dispatch vehicle at hubB (new command clears redoStack)")
    invoker.executeCommand(DispatchVehicleCommand(hubB, vehicle, dispatchUseCase))
    printState(context, "after dispatch")
    val redoAfterNewCommand = invoker.redo()
    println("  redo() after new command → $redoAfterNewCommand (should be false, redoStack was cleared)")

    println("\nStep 7: undoSteps($EXCESSIVE_UNDO_STEPS) — request more steps than available")
    val undoneAll = invoker.undoSteps(EXCESSIVE_UNDO_STEPS)
    println("  performed=$undoneAll (capped at actual undoStack size)")
    printState(context, "after undoSteps($EXCESSIVE_UNDO_STEPS)")

    println("\nStep 8: Final verification — should match the very beginning state")
    finalVerification(context)
}

private fun assignThreePackagesToHubA(context: ScenarioContext) = with(context) {
    invoker.executeCommand(AssignPackageToQueueCommand(hubA, p1, assignUseCase))
    invoker.executeCommand(AssignPackageToQueueCommand(hubA, p2, assignUseCase))
    invoker.executeCommand(AssignPackageToQueueCommand(hubA, p3, assignUseCase))
    printState(context, "after 3 assigns")
}

private fun finalVerification(context: ScenarioContext) = with(context) {
    val isHubAQueueEmpty = hubA.getCargoQueue().isEmpty()
    val isHubAFleetCorrect = hubA.getStationedVehicles().map { it.id } == listOf("V1")
    val isHubBFleetEmpty = hubB.getStationedVehicles().isEmpty()

    if (isHubAQueueEmpty && isHubAFleetCorrect && isHubBFleetEmpty) {
        println("[PASS] Final verification successful: State matches the very beginning.")
    } else {
        println("[FAIL] Final verification failed: State does not match the very beginning.")
    }
}



private fun printState(context: ScenarioContext, label: String) = with(context) {
    println(
        "  [$label] hubA.queue=${hubA.getCargoQueue().map { it.id }}, " +
                "hubA.fleet=${hubA.getStationedVehicles().map { it.id }}, " +
                "hubB.fleet=${hubB.getStationedVehicles().map { it.id }}, " +
                "undoStack=${invoker.undoStackSize}, redoStack=${invoker.redoStackSize}"
    )
}
