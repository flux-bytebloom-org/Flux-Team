package org.byte_bloom.flux.domain.logic.assignment

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Vehicle
import kotlin.math.abs

class PackageAssignmentRing(
    private val vehicles: MutableList<Vehicle>
) {

    private companion object {
        const val CIRCLE_SIZE = 100
        const val FIRST_VEHICLE_SLOT = 15
        const val SECOND_VEHICLE_SLOT = 40
        const val THIRD_VEHICLE_SLOT = 65
        const val FOURTH_VEHICLE_SLOT = 90
        val PREDEFINED_SLOTS = listOf(
            FIRST_VEHICLE_SLOT,
            SECOND_VEHICLE_SLOT,
            THIRD_VEHICLE_SLOT,
            FOURTH_VEHICLE_SLOT
        )
        const val REQUIRED_VEHICLE_COUNT = 4
        const val NEXT_SLOT_OFFSET = 1
    }

    private val vehicleSlots: MutableMap<Int, Vehicle> = mutableMapOf()
    private val currentAssignments: MutableMap<Vehicle, MutableList<Package>> = mutableMapOf()

    init {
        setupVehiclePositions()
    }

    fun assignPackages(packages: List<Package>): Map<Vehicle, List<Package>> {
        currentAssignments.clear()
        vehicleSlots.values.forEach { currentAssignments[it] = mutableListOf() }

        packages.forEach { pkg ->
            val slot = abs(pkg.id.hashCode() % CIRCLE_SIZE)
            val vehicle = findVehicleClockwise(slot)
                ?: error("No vehicle available for package ${pkg.id}")
            currentAssignments.getValue(vehicle).add(pkg)
        }

        return currentAssignments.mapValues { it.value.toList() }
    }

    fun handleBreakdown(brokenVehicle: Vehicle): Map<Vehicle, List<Package>> {
        val before = currentAssignments.mapValues { it.value.toList() }
        val brokenSlot = vehicleSlots.entries
            .firstOrNull { it.value == brokenVehicle }
            ?.key
            ?: return before

        vehicleSlots.entries
            .firstOrNull { it.value == brokenVehicle }
            ?.key
            ?.let { vehicleSlots.remove(it) }
        vehicles.remove(brokenVehicle)

        val packagesToMove = currentAssignments.remove(brokenVehicle).orEmpty()
        if (packagesToMove.isNotEmpty()) {
            val nextVehicle = findVehicleClockwise(brokenSlot + NEXT_SLOT_OFFSET)
            if (nextVehicle != null) {
                currentAssignments
                    .getOrPut(nextVehicle) { mutableListOf() }
                    .addAll(packagesToMove)
            }
        }

        val after = currentAssignments.mapValues { it.value.toList() }
        verifyNonMigration(before, after, brokenVehicle)
        return after
    }

    private fun setupVehiclePositions() {
        require(vehicles.size >= REQUIRED_VEHICLE_COUNT) {
            "At least $REQUIRED_VEHICLE_COUNT vehicles required"
        }

        PREDEFINED_SLOTS
            .zip(vehicles.take(REQUIRED_VEHICLE_COUNT))
            .forEach { (slot, vehicle) ->
                vehicleSlots[slot] = vehicle
                currentAssignments[vehicle] = mutableListOf()
            }
    }

    private fun findVehicleClockwise(packageSlot: Int): Vehicle? {
        if (vehicleSlots.isEmpty()) return null

        val sortedSlots = vehicleSlots.keys.sorted()
        val nextSlot = sortedSlots.firstOrNull { it >= packageSlot }
            ?: sortedSlots.first()

        return vehicleSlots[nextSlot]
    }

    private fun verifyNonMigration(
        before: Map<Vehicle, List<Package>>,
        after: Map<Vehicle, List<Package>>,
        brokenVehicle: Vehicle
    ) {
        var allStable = true

        before
            .filterKeys { it != brokenVehicle }
            .forEach { (vehicle, packagesBefore) ->
                val packagesAfter = after[vehicle].orEmpty()
                val isStable = packagesBefore == packagesAfter
                val status = if (isStable) "[PASS]" else "[FAIL]"
                println("$status Vehicle ${vehicle.id} packages stable: $isStable (${packagesBefore.size} packages)")
                if (!isStable) allStable = false
            }

        val status = if (allStable) "[PASS]" else "[FAIL]"
        val message = if (allStable) {
            "Only broken vehicle packages were re-routed"
        } else {
            "Some healthy vehicle packages were re-shuffled"
        }
        println("$status $message")
    }
}
