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

fun testCommandPattern() {
    println("\n--- Week 5 - subtask 5 - Testing Command Pattern Dispatch Panel ---")

    val hub = Warehouse("H1", "Main Hub", "ZoneA", DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    // سعة 30 عشان الـ dispatch يشيل P1+P2 بس ويسيب P3 — هيك منكشف خطأ الترتيب في الـ undo
    val vehicle = Vehicle("V1", hub, VEHICLE_CAPACITY_KG, 5.0)

    val p1 = Package("P1", 10.0, hub, hub, Priority.URGENT)
    val p2 = Package("P2", 20.0, hub, hub, Priority.STANDARD)
    val p3 = Package("P3", 30.0, hub, hub, Priority.LOW)
    listOf(p1, p2, p3).forEach(hub::addPackage)

    val queueBeforeAnyCommand = hub.getCargoQueue()

    val invoker = CommandInvoker()



    // الأمر الأول
    val p4 = Package("P4", 15.0, hub, hub, Priority.STANDARD)
    invoker.executeCommand(AssignPackageToQueueCommand(hub, p4))
    println("After AssignPackage(P4): ${hub.getCargoQueue().map { it.id }}")

    // الأمر الثاني
    invoker.executeCommand(DispatchVehicleCommand(hub, vehicle))
    println("After Dispatch(V1): ${hub.getCargoQueue().map { it.id }} (historySize=${invoker.historySize})")

    // تراجع عن الأخير (Dispatch)
    val undoDispatchResult = invoker.undo()
    val afterUndoDispatch = hub.getCargoQueue()
    println("Undo dispatch → $undoDispatchResult: ${afterUndoDispatch.map { it.id }}")

    // تراجع عن الأول (Assign)
    val undoAssignResult = invoker.undo()
    val afterUndoAssign = hub.getCargoQueue()
    println("Undo assign → $undoAssignResult: ${afterUndoAssign.map { it.id }}")

}
