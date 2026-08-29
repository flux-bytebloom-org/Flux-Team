package org.byte_bloom.flux.ui.utils

import org.byte_bloom.flux.domain.logic.assignment.PackageAssignmentRing
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Vehicle

fun drowPackageAssignmentRing() {
    println("--- Package Assignment Ring Test ---")

    val vehicles = MetaRingData.createVehicles()
    val packages = MetaRingData.createPackages(vehicles)
    val ring = PackageAssignmentRing(vehicles)

    val initialAssignments = ring.assignPackages(packages)
    println("\nInitial Assignments:")
    printAssignments(initialAssignments)

    val brokenVehicle = vehicles[MetaRingData.BROKEN_VEHICLE_INDEX]
    println("\nSimulating breakdown of ${brokenVehicle.id}...")
    val updatedAssignments = ring.handleBreakdown(brokenVehicle)

    println("\nAssignments after breakdown:")
    printAssignments(updatedAssignments)
}

private fun printAssignments(assignments: Map<Vehicle, List<Package>>) {
    assignments.forEach { (vehicle, pkgs) ->
        println("  ${vehicle.id}: ${pkgs.map { it.id }}")
    }
}

