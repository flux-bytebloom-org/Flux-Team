package org.byte_bloom.flux.domain.state

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.exception.LogisticsException.BusinessLogicException.IllegalStateTransitionException
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

private const val CREATED = "CreatedState"
private const val ASSIGNED = "AssignedToVehicleState"
private const val IN_TRANSIT = "InTransitState"
private const val DELIVERED = "DeliveredState"
private const val DELIVERY_FAILED = "DeliveryFailedState"
private const val ILLEGAL_TRANSITION_PREFIX = "Illegal state transition: "

class ShipmentStateTest {

    private fun allTransitionsOf(state: ShipmentState): List<() -> ShipmentState> = listOf(
        { state.assignToVehicle() },
        { state.startTransit() },
        { state.markDelivered() },
        { state.markFailed() }
    )

    @Test
    fun `given created state when assign to vehicle then moves to assigned state`() {
        // Given
        val state = CreatedState

        // When
        val next = state.assignToVehicle()

        // Then
        assertThat(next).isEqualTo(AssignedToVehicleState)
    }

    @Test
    fun `given assigned state when start transit then moves to in transit state`() {
        // Given
        val state = AssignedToVehicleState

        // When
        val next = state.startTransit()

        // Then
        assertThat(next).isEqualTo(InTransitState)
    }

    @Test
    fun `given in transit state when mark delivered then moves to delivered state`() {
        // Given
        val state = InTransitState

        // When
        val next = state.markDelivered()

        // Then
        assertThat(next).isEqualTo(DeliveredState)
    }

    @Test
    fun `given in transit state when mark failed then moves to delivery failed state`() {
        // Given
        val state = InTransitState

        // When
        val next = state.markFailed()

        // Then
        assertThat(next).isEqualTo(DeliveryFailedState)
    }

    @Test
    fun `given created state when start transit then throws illegal transition`() {
        // Given
        val state = CreatedState

        // When
        val exception = assertFailsWith<IllegalStateTransitionException> { state.startTransit() }

        // Then
        assertThat(exception.from).isEqualTo(CREATED)
        assertThat(exception.to).isEqualTo(IN_TRANSIT)
    }

    @Test
    fun `given created state when mark delivered then throws illegal transition`() {
        // Given
        val state = CreatedState

        // When
        val exception = assertFailsWith<IllegalStateTransitionException> { state.markDelivered() }

        // Then
        assertThat(exception.to).isEqualTo(DELIVERED)
    }

    @Test
    fun `given assigned state when mark delivered then throws illegal transition`() {
        // Given
        val state = AssignedToVehicleState

        // When
        val exception = assertFailsWith<IllegalStateTransitionException> { state.markDelivered() }

        // Then
        assertThat(exception.from).isEqualTo(ASSIGNED)
    }

    @Test
    fun `given assigned state when assign again then throws illegal transition`() {
        // Given
        val state = AssignedToVehicleState

        // When
        val exception = assertFailsWith<IllegalStateTransitionException> { state.assignToVehicle() }

        // Then
        assertThat(exception.to).isEqualTo(ASSIGNED)
    }

    @Test
    fun `given delivered state when any transition then throws illegal transition`() {
        // Given
        val transitions = allTransitionsOf(DeliveredState)

        // When
        val failures = transitions.map { transition ->
            assertFailsWith<IllegalStateTransitionException> { transition() }
        }

        // Then
        assertThat(failures.map { it.from }.toSet()).containsExactly(DELIVERED)
    }

    @Test
    fun `given delivery failed state when any transition then throws illegal transition`() {
        // Given
        val transitions = allTransitionsOf(DeliveryFailedState)

        // When
        val failures = transitions.map { transition ->
            assertFailsWith<IllegalStateTransitionException> { transition() }
        }

        // Then
        assertThat(failures.map { it.from }.toSet()).containsExactly(DELIVERY_FAILED)
    }

    @Test
    fun `given illegal transition when thrown then message names both states`() {
        // Given
        val state = CreatedState

        // When
        val exception = assertFailsWith<IllegalStateTransitionException> { state.markFailed() }

        // Then
        assertThat(exception).hasMessageThat().isEqualTo("$ILLEGAL_TRANSITION_PREFIX$CREATED -> $DELIVERY_FAILED")
    }

    //complete state machine test to ensure all states and actions are covered
    @Test
    fun `every state and action pair either follows the lifecycle or is rejected`() {
        val states = listOf<ShipmentState>(
            CreatedState, AssignedToVehicleState, InTransitState, DeliveredState, DeliveryFailedState
        )

        states.forEach { state ->
            actions.forEach { (actionName, action) ->
                val expected = legal[state to actionName]
                if (expected != null) {
                    assertThat(action(state)).isEqualTo(expected)
                } else {
                    assertFailsWith<IllegalStateTransitionException>("${state.name}.$actionName") { action(state) }
                }
            }
        }
    }

    private val actions: Map<String, (ShipmentState) -> ShipmentState> = mapOf(
        "assignToVehicle" to { it.assignToVehicle() },
        "startTransit" to { it.startTransit() },
        "markDelivered" to { it.markDelivered() },
        "markFailed" to { it.markFailed() }
    )

    private val legal: Map<Pair<ShipmentState, String>, ShipmentState> = mapOf(
        (CreatedState to "assignToVehicle") to AssignedToVehicleState,
        (AssignedToVehicleState to "startTransit") to InTransitState,
        (InTransitState to "markDelivered") to DeliveredState,
        (InTransitState to "markFailed") to DeliveryFailedState
    )

}
