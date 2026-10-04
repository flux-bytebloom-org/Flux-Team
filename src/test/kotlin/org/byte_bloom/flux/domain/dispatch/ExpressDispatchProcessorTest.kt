package org.byte_bloom.flux.domain.dispatch

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.exception.LogisticsException.ValidationException.InvalidTransitLoadException
import org.byte_bloom.flux.domain.state.CreatedState
import org.byte_bloom.flux.domain.state.InTransitState
import org.byte_bloom.flux.domain.state.Shipment
import org.byte_bloom.flux.domain.testdata.aVehicle
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertFailsWith

private const val VEHICLE_CAPACITY_KG = 100.0
private const val PACKAGE_KG = 30.0
private const val OVERWEIGHT_PACKAGE_KG = 120.0

private const val VEHICLE_ID = "V-1"
private const val PACKAGE_ID = "PKG-7"
private const val EXPRESS_TAG = "[EXPRESS]"
private const val IN_TRANSIT = "InTransitState"

class ExpressDispatchProcessorTest {

    private val processor = ExpressDispatchProcessor()
    private val vehicle = aVehicle(id = VEHICLE_ID, capacity = VEHICLE_CAPACITY_KG)

    private fun captureOutput(action: () -> Unit): String {
        val output = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(output))
        try {
            action()
        } finally {
            System.setOut(originalOut)
        }
        return output.toString()
    }

    @Test
    fun `given package that fits when process then shipment is in transit`() {
        // Given
        val shipment = Shipment(createTestPackage(id = PACKAGE_ID, weight = PACKAGE_KG))

        // When
        captureOutput { processor.process(shipment, vehicle) }

        // Then
        assertThat(shipment.state).isEqualTo(InTransitState)
    }

    @Test
    fun `given package that fits when process then sends express notification`() {
        // Given
        val shipment = Shipment(createTestPackage(id = PACKAGE_ID, weight = PACKAGE_KG))

        // When
        val output = captureOutput { processor.process(shipment, vehicle) }

        // Then
        assertThat(output).contains(EXPRESS_TAG)
        assertThat(output).contains(PACKAGE_ID)
        assertThat(output).contains(VEHICLE_ID)
        assertThat(output).contains(IN_TRANSIT)
    }

    @Test
    fun `given package heavier than capacity when process then throws without notification`() {
        // Given
        val shipment = Shipment(createTestPackage(id = PACKAGE_ID, weight = OVERWEIGHT_PACKAGE_KG))

        // When
        val output = captureOutput {
            assertFailsWith<InvalidTransitLoadException> { processor.process(shipment, vehicle) }
        }

        // Then
        assertThat(shipment.state).isEqualTo(CreatedState)
        assertThat(output).isEmpty()
    }
}
