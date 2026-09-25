package org.byte_bloom.flux.ui.utils

import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.DispatchedVehicle
import org.byte_bloom.flux.domain.usecase.GreedyDispatchRequest
import org.byte_bloom.flux.domain.usecase.GreedyFleetDispatchUseCase

fun testGreedyFleetDispatchUseCase() {
    println("\n--- Sub-Task 5: GreedyFleetDispatchUseCase self-test ---")
    testCoversTargetZonesGreedily()
    testReportsUncoveredZonesWhenNoVehicleCanReachThem()
    testHandlesEmptyVehicleList()
    println("All GreedyFleetDispatchUseCase checks passed.")
}

private fun dummyWarehouse(id: String, zone: RegionalZone) =
    Warehouse(id = id, name = id, regionalZone = zone, latitude = 0.0, longitude = 0.0)

private fun dummyDispatched(vehicleId: String, hub: Warehouse, path: List<Warehouse>) =
    DispatchedVehicle(
        vehicle = Vehicle(id = vehicleId, currentHub = hub, maxCapacityKg = 500.0, costPerKm = 2.0),
        loadedPackages = emptyList(),
        totalWeight = 0.0,
        path = path
    )

private fun testCoversTargetZonesGreedily() {
    val whN = dummyWarehouse("WH-N", RegionalZone.NORTH)
    val whE = dummyWarehouse("WH-E", RegionalZone.EAST)
    val whS = dummyWarehouse("WH-S", RegionalZone.SOUTH)
    val whW = dummyWarehouse("WH-W", RegionalZone.WEST)
    val whC = dummyWarehouse("WH-C", RegionalZone.CENTRAL)

    val dispatched = listOf(
        dummyDispatched("TRK-001", whN, listOf(whN, whE)),
        dummyDispatched("TRK-002", whS, listOf(whS, whW, whC)),
        dummyDispatched("TRK-003", whN, listOf(whN, whS)),
        dummyDispatched("TRK-004", whE, listOf(whE))
    )

    val request = GreedyDispatchRequest(
        targetZones = setOf(RegionalZone.NORTH, RegionalZone.SOUTH, RegionalZone.EAST, RegionalZone.WEST, RegionalZone.CENTRAL),
        dispatchedVehicles = dispatched
    )

    val result = GreedyFleetDispatchUseCase()(request)

    check(result.uncoveredZones.isEmpty()) { "expected all zones covered, got ${result.uncoveredZones} uncovered" }
    // NOTE: only checks which zones end up covered — Greedy isn't guaranteed to be the
    // mathematically minimal fleet, just a close, fast heuristic.
    check(result.selectedVehicles.map { it.id } == listOf("TRK-002", "TRK-001")) {
        "expected TRK-002 then TRK-001, got ${result.selectedVehicles.map { it.id }}"
    }
    println("  [PASS] greedily covers all target zones in the expected order")
}

private fun testReportsUncoveredZonesWhenNoVehicleCanReachThem() {
    val whN = dummyWarehouse("WH-N", RegionalZone.NORTH)
    val whS = dummyWarehouse("WH-S", RegionalZone.SOUTH)

    val dispatched = listOf(
        dummyDispatched("TRK-010", whN, listOf(whN)),
        dummyDispatched("TRK-011", whS, listOf(whS))
    )

    val request = GreedyDispatchRequest(
        targetZones = setOf(RegionalZone.NORTH, RegionalZone.SOUTH, RegionalZone.CENTRAL),
        dispatchedVehicles = dispatched
    )

    val result = GreedyFleetDispatchUseCase()(request)

    check(result.uncoveredZones == setOf(RegionalZone.CENTRAL)) { "expected CENTRAL uncovered, got ${result.uncoveredZones}" }
    println("  [PASS] reports zones nobody can reach, instead of crashing")
}

private fun testHandlesEmptyVehicleList() {
    val request = GreedyDispatchRequest(setOf(RegionalZone.NORTH, RegionalZone.SOUTH), emptyList())
    val result = GreedyFleetDispatchUseCase()(request)
    check(result.selectedVehicles.isEmpty() && result.uncoveredZones == request.targetZones) {
        "expected everything uncovered with no vehicles"
    }
    println("  [PASS] handles an empty dispatched-vehicle list without crashing")
}