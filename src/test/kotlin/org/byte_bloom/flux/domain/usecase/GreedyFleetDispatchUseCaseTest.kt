package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.RegionalZone.CENTRAL
import org.byte_bloom.flux.domain.model.RegionalZone.EAST
import org.byte_bloom.flux.domain.model.RegionalZone.NORTH
import org.byte_bloom.flux.domain.model.RegionalZone.SOUTH
import org.byte_bloom.flux.domain.model.RegionalZone.UnKNOWN
import org.byte_bloom.flux.domain.model.RegionalZone.WEST
import org.byte_bloom.flux.domain.request.GreedyDispatchRequest
import org.byte_bloom.flux.domain.response.DispatchedVehicle
import org.byte_bloom.flux.domain.testdata.aVehicle
import org.byte_bloom.flux.domain.testdata.aWarehouse
import org.junit.jupiter.api.Test

class GreedyFleetDispatchUseCaseTest {

    private val useCase = GreedyFleetDispatchUseCase()

    @Test
    fun `covers all target zones by picking the vehicle with the most uncovered zones each round`() {
        // Given
        val request = GreedyDispatchRequest(
            targetZones = setOf(NORTH, SOUTH, EAST, WEST, CENTRAL),
            dispatchedVehicles = listOf(
                aDispatched("TRK-001", NORTH, EAST),
                aDispatched("TRK-002", SOUTH, WEST, CENTRAL),
                aDispatched("TRK-003", NORTH, SOUTH),
                aDispatched("TRK-004", EAST)
            )
        )

        // When
        val result = useCase(request)

        // Then
        assertThat(result.selectedVehicles.map { it.id }).containsExactly("TRK-002", "TRK-001").inOrder()
        assertThat(result.uncoveredZones).isEmpty()
    }

    @Test
    fun `reports zones that no vehicle can reach instead of failing`() {
        // Given
        val request = GreedyDispatchRequest(
            targetZones = setOf(NORTH, SOUTH, CENTRAL),
            dispatchedVehicles = listOf(
                aDispatched("TRK-010", NORTH),
                aDispatched("TRK-011", SOUTH)
            )
        )

        // When
        val result = useCase(request)

        // Then
        assertThat(result.selectedVehicles.map { it.id }).containsExactly("TRK-010", "TRK-011")
        assertThat(result.uncoveredZones).containsExactly(CENTRAL)
    }

    @Test
    fun `leaves every zone uncovered when there are no vehicles`() {
        // Given
        val request = GreedyDispatchRequest(setOf(NORTH, SOUTH), emptyList())

        // When
        val result = useCase(request)

        // Then
        assertThat(result.selectedVehicles).isEmpty()
        assertThat(result.uncoveredZones).containsExactly(NORTH, SOUTH)
    }

    @Test
    fun `selects nothing when there are no target zones`() {
        // Given
        val request = GreedyDispatchRequest(
            targetZones = emptySet(),
            dispatchedVehicles = listOf(aDispatched("TRK-001", NORTH))
        )

        // When
        val result = useCase(request)

        // Then
        assertThat(result.selectedVehicles).isEmpty()
        assertThat(result.uncoveredZones).isEmpty()
    }

    @Test
    fun `does not select a vehicle that covers no remaining zone`() {
        // Given: after TRK-A only CENTRAL is left, and TRK-B (subset of TRK-A) can't add it
        val request = GreedyDispatchRequest(
            targetZones = setOf(NORTH, EAST, CENTRAL),
            dispatchedVehicles = listOf(
                aDispatched("TRK-A", NORTH, EAST),
                aDispatched("TRK-B", NORTH)
            )
        )

        // When
        val result = useCase(request)

        // Then
        assertThat(result.selectedVehicles.map { it.id }).containsExactly("TRK-A")
        assertThat(result.uncoveredZones).containsExactly(CENTRAL)
    }

    @Test
    fun `ranks vehicles only by the target zones they cover`() {
        // Given: TRK-B visits many zones, but only NORTH is a target
        val request = GreedyDispatchRequest(
            targetZones = setOf(NORTH),
            dispatchedVehicles = listOf(
                aDispatched("TRK-B", EAST, SOUTH, WEST, UnKNOWN),
                aDispatched("TRK-A", NORTH)
            )
        )

        // When
        val result = useCase(request)

        // Then
        assertThat(result.selectedVehicles.map { it.id }).containsExactly("TRK-A")
        assertThat(result.uncoveredZones).isEmpty()
    }

    @Test
    fun `is a heuristic that may select more vehicles than the minimum`() {
        // Given: TRK-X + TRK-Y alone would cover everything (2 vehicles),
        // but greedy takes TRK-T first because it ties for the most zones and comes first.
        val request = GreedyDispatchRequest(
            targetZones = setOf(NORTH, EAST, SOUTH, WEST, CENTRAL),
            dispatchedVehicles = listOf(
                aDispatched("TRK-T", NORTH, EAST, CENTRAL),
                aDispatched("TRK-X", NORTH, SOUTH, CENTRAL),
                aDispatched("TRK-Y", EAST, WEST)
            )
        )

        // When
        val result = useCase(request)

        // Then
        assertThat(result.selectedVehicles.map { it.id })
            .containsExactly("TRK-T", "TRK-X", "TRK-Y")
            .inOrder()
        assertThat(result.uncoveredZones).isEmpty()
    }

    // ---------- Helpers ----------

    private fun aDispatched(vehicleId: String, vararg zones: RegionalZone) = DispatchedVehicle(
        vehicle = aVehicle(id = vehicleId),
        loadedPackages = emptyList(),
        totalWeight = 0.0,
        path = zones.mapIndexed { index, zone -> aWarehouse(id = "WH-$vehicleId-$index", zone = zone) }
    )
}
