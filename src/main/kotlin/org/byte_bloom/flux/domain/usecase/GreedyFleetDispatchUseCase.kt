package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.response.DispatchedVehicle
import org.byte_bloom.flux.domain.response.GreedyDispatchResult

data class GreedyDispatchRequest(
    val targetZones: Set<RegionalZone>,
    val dispatchedVehicles: List<DispatchedVehicle>
)

/**
 * Solves the Set-Covering Problem with a Greedy heuristic: given target zones and a
 * list of dispatched vehicles (each covering the RegionalZones on the path
 * DispatchVehicleUseCase already computed for it via Dijkstra), iteratively picks
 * the vehicle covering the most still-uncovered zones until every zone is covered
 * or nobody can add more.
 *
 * Why Greedy over Brute Force:
 * - Brute force checks every subset of N vehicles -> O(2^N), exponential.
 * - Greedy runs at most N rounds, scanning up to N remaining vehicles each round
 *   -> O(N^2), polynomial. Not guaranteed to be the mathematically smallest fleet,
 *   but proven close to optimal and fast enough to run live.
 */
class GreedyFleetDispatchUseCase {

    operator fun invoke(request: GreedyDispatchRequest): GreedyDispatchResult {
        val remainingZones = request.targetZones.toMutableSet()
        val remainingVehicles = request.dispatchedVehicles.toMutableList()
        val selected = mutableListOf<Vehicle>()

        while (remainingZones.isNotEmpty() && remainingVehicles.isNotEmpty()) {
            val best = pickBestVehicle(remainingVehicles, remainingZones) ?: break

            val newlyCovered = coveredZonesOf(best).intersect(remainingZones)
            if (newlyCovered.isEmpty()) break

            selected += best.vehicle
            remainingZones -= newlyCovered
            remainingVehicles -= best
        }

        return GreedyDispatchResult(selectedVehicles = selected, uncoveredZones = remainingZones)
    }

    private fun pickBestVehicle(
        vehicles: List<DispatchedVehicle>,
        remainingZones: Set<RegionalZone>
    ): DispatchedVehicle? =
        vehicles.maxByOrNull { dv -> coveredZonesOf(dv).count { zone -> zone in remainingZones } }

    private fun coveredZonesOf(dispatched: DispatchedVehicle): Set<RegionalZone> =
        dispatched.path.map { warehouse -> warehouse.regionalZone }.toSet()
}