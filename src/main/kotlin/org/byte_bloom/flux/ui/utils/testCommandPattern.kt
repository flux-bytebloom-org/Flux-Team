package org.byte_bloom.flux.ui.utils

import org.byte_bloom.flux.domain.logic.command.AssignPackageToQueueCommand
import org.byte_bloom.flux.domain.logic.command.CommandInvoker
import org.byte_bloom.flux.domain.logic.command.DispatchVehicleCommand
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

private const val DEFAULT_LATITUDE = 0.0
private const val DEFAULT_LONGITUDE = 0.0
private const val VEHICLE_CAPACITY_KG = 30.0
private const val VEHICLE_SPEED_KMH = 5.0
private const val FIRST_PACKAGE_WEIGHT_KG = 10.0
private const val SECOND_PACKAGE_WEIGHT_KG = 20.0
private const val THIRD_PACKAGE_WEIGHT_KG = 30.0
private const val FOURTH_PACKAGE_WEIGHT_KG = 15.0

fun testCommandPattern() {
    println("\n--- Week 5 - Subtask 5 - Testing Command Pattern Dispatch Panel ---")

    val hub = Warehouse(
        "H1",
        "Main Hub",
        "ZoneA",
        DEFAULT_LATITUDE,
        DEFAULT_LONGITUDE
    )

    val vehicle = Vehicle(
        "V1",
        hub,
        VEHICLE_CAPACITY_KG,
        VEHICLE_SPEED_KMH
    )

    val invoker = CommandInvoker()

    addInitialPackages(hub)

    executeCommands(hub, vehicle, invoker)
    testUndoRedo(hub, invoker)
    testMultiStepTimeTravel(hub, invoker)
}

private fun addInitialPackages(hub: Warehouse) {
    val p1 = Package(
        "P1",
        FIRST_PACKAGE_WEIGHT_KG,
        hub,
        hub,
        Priority.URGENT
    )

    val p2 = Package(
        "P2",
        SECOND_PACKAGE_WEIGHT_KG,
        hub,
        hub,
        Priority.STANDARD
    )

    val p3 = Package(
        "P3",
        THIRD_PACKAGE_WEIGHT_KG,
        hub,
        hub,
        Priority.LOW
    )

    listOf(p1, p2, p3).forEach(hub::addPackage)
}

private fun executeCommands(
    hub: Warehouse,
    vehicle: Vehicle,
    invoker: CommandInvoker
) {
    val p4 = Package(
        "P4",
        FOURTH_PACKAGE_WEIGHT_KG,
        hub,
        hub,
        Priority.STANDARD
    )

    invoker.executeCommand(
        AssignPackageToQueueCommand(hub, p4)
    )

    println("After AssignPackage(P4): ${hub.getCargoQueue().map { it.id }}")

    invoker.executeCommand(
        DispatchVehicleCommand(hub, vehicle)
    )

    println(
        "After Dispatch(V1): ${hub.getCargoQueue().map { it.id }} " +
                "(historySize=${invoker.historySize})"
    )
}

private fun testUndoRedo(
    hub: Warehouse,
    invoker: CommandInvoker
) {
    val undoDispatchResult = invoker.undo()

    println(
        "Undo dispatch → $undoDispatchResult: " +
                "${hub.getCargoQueue().map { it.id }}"
    )

    val undoAssignResult = invoker.undo()

    println(
        "Undo assign → $undoAssignResult: " +
                "${hub.getCargoQueue().map { it.id }}"
    )

    val redoAssignResult = invoker.redo()

    println(
        "Redo assign → $redoAssignResult: " +
                "${hub.getCargoQueue().map { it.id }}"
    )

    val redoDispatchResult = invoker.redo()

    println(
        "Redo dispatch → $redoDispatchResult: " +
                "${hub.getCargoQueue().map { it.id }}"
    )
}

private fun testMultiStepTimeTravel(
    hub: Warehouse,
    invoker: CommandInvoker
) {
    println("\n--- Multi-step time travel ---")

    invoker.undoSteps(2)

    println(
        "After undoSteps(2): ${hub.getCargoQueue().map { it.id }}"
    )

    invoker.redoSteps(2)

    println(
        "After redoSteps(2): ${hub.getCargoQueue().map { it.id }}"
    )
}