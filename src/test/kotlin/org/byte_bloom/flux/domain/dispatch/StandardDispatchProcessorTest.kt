package org.byte_bloom.flux.domain.dispatch

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.exception.LogisticsException.BusinessLogicException.IllegalStateTransitionException
import org.byte_bloom.flux.domain.exception.LogisticsException.ValidationException.InvalidPackageWeightException
import org.byte_bloom.flux.domain.exception.LogisticsException.BusinessLogicException.VehicleCapacityExceededException
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
private const val LIGHT_PACKAGE_KG = 40.0
private const val HEAVY_PACKAGE_KG = 70.0
private const val OVERWEIGHT_PACKAGE_KG = 150.0

private const val FIRST_VEHICLE_ID = "V-1"
private const val SECOND_VEHICLE_ID = "V-2"
private const val FIRST_PACKAGE_ID = "PKG-1"
private const val SECOND_PACKAGE_ID = "PKG-2"
private const val IN_TRANSIT = "InTransitState"

class StandardDispatchProcessorTest {

    private val processor = StandardDispatchProcessor()
    private val vehicle = aVehicle(id = FIRST_VEHICLE_ID, capacity = VEHICLE_CAPACITY_KG)

    private fun shipment(id: String = FIRST_PACKAGE_ID, weight: Double? = LIGHT_PACKAGE_KG) =
        Shipment(createTestPackage(id = id, weight = weight))

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
        val shipment = shipment()

        // When
        processor.process(shipment, vehicle)

        // Then
        assertThat(shipment.state).isEqualTo(InTransitState)
    }

    @Test
    fun `given package exactly at capacity when process then shipment is in transit`() {
        // Given
        val shipment = shipment(weight = VEHICLE_CAPACITY_KG)

        // When
        processor.process(shipment, vehicle)

        // Then
        assertThat(shipment.state).isEqualTo(InTransitState)
    }

    @Test
    fun `given package heavier than capacity when process then throws and keeps created state`() {
        // Given
        val shipment = shipment(weight = OVERWEIGHT_PACKAGE_KG)

        // When
        assertFailsWith<VehicleCapacityExceededException> { processor.process(shipment, vehicle) }

        // Then
        assertThat(shipment.state).isEqualTo(CreatedState)
    }

    @Test
    fun `given package without weight when process then throws InvalidPackageWeightException`() {
        // Given
        val shipment = shipment(weight = null)

        // When
        assertFailsWith<InvalidPackageWeightException> { processor.process(shipment, vehicle) }

        // Then
        assertThat(shipment.state).isEqualTo(CreatedState)
    }

    @Test
    fun `given vehicle partly reserved when next package exceeds remaining then throws`() {
        // Given
        processor.process(shipment(id = FIRST_PACKAGE_ID, weight = HEAVY_PACKAGE_KG), vehicle)
        val second = shipment(id = SECOND_PACKAGE_ID, weight = LIGHT_PACKAGE_KG)

        // When
        assertFailsWith<VehicleCapacityExceededException> { processor.process(second, vehicle) }

        // Then
        assertThat(second.state).isEqualTo(CreatedState)
    }

    @Test
    fun `given different vehicles when process then capacity is reserved per vehicle`() {
        // Given
        val otherVehicle = aVehicle(id = SECOND_VEHICLE_ID, capacity = VEHICLE_CAPACITY_KG)
        processor.process(shipment(id = FIRST_PACKAGE_ID, weight = HEAVY_PACKAGE_KG), vehicle)
        val second = shipment(id = SECOND_PACKAGE_ID, weight = HEAVY_PACKAGE_KG)

        // When
        processor.process(second, otherVehicle)

        // Then
        assertThat(second.state).isEqualTo(InTransitState)
    }

    @Test
    fun `given shipment already in transit when process again then throws illegal transition`() {
        // Given
        val shipment = shipment()
        processor.process(shipment, vehicle)

        // When
        val exception = assertFailsWith<IllegalStateTransitionException> { processor.process(shipment, vehicle) }

        // Then
        assertThat(exception.from).isEqualTo(IN_TRANSIT)
    }

    @Test
    fun `given standard processor when process then prints no notification`() {
        // Given
        val shipment = shipment()

        // When
        val output = captureOutput { processor.process(shipment, vehicle) }

        // Then
        assertThat(output).isEmpty()
    }

    @Test
    fun `given a rejected second dispatch when next package is processed then capacity was not consumed twice`() {
        // Given
        val first = shipment(id = FIRST_PACKAGE_ID, weight = LIGHT_PACKAGE_KG)      // 40 reserved
        processor.process(first, vehicle)
        assertFailsWith<IllegalStateTransitionException> { processor.process(first, vehicle) }
        val second = shipment(id = SECOND_PACKAGE_ID, weight = 50.0)                // 40 + 50 <= 100

        // When
        processor.process(second, vehicle)

        // Then
        assertThat(second.state).isEqualTo(InTransitState)
    }

    @Test
    fun `given negative weight when process then throws InvalidPackageWeightException`() {
        // Given: -1.0 is what the CSV parser produces for an unreadable weight
        val shipment = shipment(weight = -1.0)

        // When
        assertFailsWith<InvalidPackageWeightException> { processor.process(shipment, vehicle) }

        // Then
        assertThat(shipment.state).isEqualTo(CreatedState)
    }
}
