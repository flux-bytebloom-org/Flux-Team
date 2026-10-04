package org.byte_bloom.flux.domain.state

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.exception.LogisticsException.BusinessLogicException.IllegalStateTransitionException
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ShipmentTest {

    private val pkg = createTestPackage()

    private fun shipmentInTransit() = Shipment(pkg).apply {
        assignToVehicle()
        startTransit()
    }

    @Test
    fun `given new shipment when created then state is created`() {
        // Given
        val shipment = Shipment(pkg)

        // When
        val state = shipment.state

        // Then
        assertThat(state).isEqualTo(CreatedState)
    }

    @Test
    fun `given new shipment when full successful lifecycle then ends delivered`() {
        // Given
        val shipment = Shipment(pkg)

        // When
        shipment.assignToVehicle()
        shipment.startTransit()
        shipment.markDelivered()

        // Then
        assertThat(shipment.state).isEqualTo(DeliveredState)
    }

    @Test
    fun `given shipment in transit when delivery fails then ends delivery failed`() {
        // Given
        val shipment = shipmentInTransit()

        // When
        shipment.markFailed()

        // Then
        assertThat(shipment.state).isEqualTo(DeliveryFailedState)
    }

    @Test
    fun `given new shipment when skipping to transit then throws and keeps created state`() {
        // Given
        val shipment = Shipment(pkg)

        // When
        assertFailsWith<IllegalStateTransitionException> { shipment.startTransit() }

        // Then
        assertThat(shipment.state).isEqualTo(CreatedState)
    }

    @Test
    fun `given delivered shipment when marked failed then throws and keeps delivered state`() {
        // Given
        val shipment = shipmentInTransit()
        shipment.markDelivered()

        // When
        assertFailsWith<IllegalStateTransitionException> { shipment.markFailed() }

        // Then
        assertThat(shipment.state).isEqualTo(DeliveredState)
    }

    @Test
    fun `given shipment when created then keeps the same package`() {
        // Given
        val shipment = Shipment(pkg)

        // When
        val shipmentPackage = shipment.pkg

        // Then
        assertThat(shipmentPackage).isSameInstanceAs(pkg)
    }
}
