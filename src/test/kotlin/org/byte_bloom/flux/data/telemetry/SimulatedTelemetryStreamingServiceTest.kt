package org.byte_bloom.flux.data.telemetry

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.VehicleTelemetry
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.testdata.aVehicle
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertFailsWith

class SimulatedTelemetryStreamingServiceTest {
    private companion object {
        const val INTERVAL_MS = 1_000L
        const val HUB_LATITUDE = 31.9
        const val HUB_LONGITUDE = 35.2
        const val MAX_DRIFT = 0.001
        const val MAX_FUEL_BURN = 0.5
        const val MAX_SPEED_KMH = 90.0
        const val CONGESTED_SPEED_LIMIT_KMH = 15.0
        const val CRUISING_SPEED_MIN_KMH = 40.0
        const val FLOAT_TOLERANCE = 1e-9
        const val FULL_FUEL = 100.0
        const val SEED = 42
        const val MANY_READINGS = 200
        const val READINGS_PER_TICK_FOR_TWO_VEHICLES = 2
    }

    private val hub = Warehouse("WH-HUB", "Hub", RegionalZone.NORTH, HUB_LATITUDE, HUB_LONGITUDE)
    private val fleet = listOf(aVehicle(id = "V-1", hub = hub), aVehicle(id = "V-2", hub = hub))

    private fun service(seed: Int = SEED, interval: Long = INTERVAL_MS): SimulatedTelemetryStreamingService {
        var ticks = 0L
        return SimulatedTelemetryStreamingService(interval, Random(seed)) { ticks++ }
    }

    @Test
    fun `given a fleet when stream then emits one reading per vehicle in order each update`() = runTest {
        // When
        val readings = service().stream(fleet).take(4).toList()

        // Then
        assertThat(readings.map { it.vehicleId }).containsExactly("V-1", "V-2", "V-1", "V-2").inOrder()
    }

    @Test
    fun `given an update interval when stream then readings are spaced by that interval`() = runTest {
        // When: three updates of two vehicles
        service().stream(fleet).take(3 * READINGS_PER_TICK_FOR_TWO_VEHICLES).toList()

        // Then: two waits happened between the three updates
        assertThat(currentTime).isEqualTo(2 * INTERVAL_MS)
    }

    @Test
    fun `given a stream that is not collected when created then nothing is produced`() = runTest {
        // Given
        var timestampCalls = 0
        val service = SimulatedTelemetryStreamingService(INTERVAL_MS, Random(SEED)) { timestampCalls++; 0L }

        // When
        val stream = service.stream(fleet)

        // Then
        assertThat(timestampCalls).isEqualTo(0)
        stream.take(1).toList()
        assertThat(timestampCalls).isEqualTo(1)
    }

    @Test
    fun `given the same stream collected twice when stream then each collection starts from fresh state`() = runTest {
        // Given
        val stream = service().stream(fleet)

        // When
        val firstRun = stream.take(MANY_READINGS).toList()
        val secondRun = stream.take(READINGS_PER_TICK_FOR_TWO_VEHICLES).toList()

        // Then
        assertThat(firstRun.last().fuelPercentage).isLessThan(FULL_FUEL - MAX_FUEL_BURN)
        assertThat(secondRun.first().fuelPercentage).isAtLeast(FULL_FUEL - MAX_FUEL_BURN)
        assertThat(secondRun.first().latitude).isWithin(MAX_DRIFT + FLOAT_TOLERANCE).of(HUB_LATITUDE)
    }

    @Test
    fun `given vehicles stationed at a hub when stream then first readings start near that hub`() = runTest {
        // When
        val readings = service().stream(fleet).take(READINGS_PER_TICK_FOR_TWO_VEHICLES).toList()

        // Then
        readings.forEach { reading ->
            assertThat(reading.latitude).isWithin(MAX_DRIFT + FLOAT_TOLERANCE).of(HUB_LATITUDE)
            assertThat(reading.longitude).isWithin(MAX_DRIFT + FLOAT_TOLERANCE).of(HUB_LONGITUDE)
        }
    }

    @Test
    fun `given many readings when stream then speed and fuel stay inside their limits`() = runTest {
        // When
        val readings = service().stream(fleet).take(MANY_READINGS).toList()

        // Then
        readings.forEach { reading ->
            assertThat(reading.speedKmh).isAtLeast(0.0)
            assertThat(reading.speedKmh).isAtMost(MAX_SPEED_KMH)
            assertThat(reading.fuelPercentage).isAtLeast(0.0)
            assertThat(reading.fuelPercentage).isAtMost(FULL_FUEL)
        }
    }

    @Test
    fun `given many readings when stream then fuel never increases for a vehicle`() = runTest {
        // When
        val readings = service().stream(fleet).take(MANY_READINGS).toList()

        // Then
        readings.groupBy { it.vehicleId }.values.forEach { vehicleReadings ->
            assertThat(vehicleReadings.zipWithNext().all { (a, b) -> b.fuelPercentage <= a.fuelPercentage }).isTrue()
        }
    }

    @Test
    fun `given many readings when stream then a vehicle moves by small steps only`() = runTest {
        // When
        val readings = service().stream(fleet).take(MANY_READINGS).toList()

        // Then
        readings.groupBy { it.vehicleId }.values.forEach { vehicleReadings ->
            vehicleReadings.zipWithNext().forEach { (a, b) ->
                assertThat(abs(b.latitude - a.latitude)).isAtMost(MAX_DRIFT + FLOAT_TOLERANCE)
                assertThat(abs(b.longitude - a.longitude)).isAtMost(MAX_DRIFT + FLOAT_TOLERANCE)
            }
        }
    }

    @Test
    fun `given many readings when stream then both congested and cruising speeds appear`() = runTest {
        // When
        val speeds = service().stream(fleet).take(MANY_READINGS).toList().map(VehicleTelemetry::speedKmh)

        // Then
        assertThat(speeds.any { it < CONGESTED_SPEED_LIMIT_KMH }).isTrue()
        assertThat(speeds.any { it >= CRUISING_SPEED_MIN_KMH }).isTrue()
    }

    @Test
    fun `given the same seed when stream then produces the same readings`() = runTest {
        // When
        val first = service(seed = 7).stream(fleet).take(20).toList()
        val second = service(seed = 7).stream(fleet).take(20).toList()

        // Then
        assertThat(first).isEqualTo(second)
    }

    @Test
    fun `given different seeds when stream then produces different readings`() = runTest {
        // When
        val first = service(seed = 1).stream(fleet).take(20).toList()
        val second = service(seed = 2).stream(fleet).take(20).toList()

        // Then
        assertThat(first).isNotEqualTo(second)
    }

    @Test
    fun `given a collector that is cancelled when stream then no more readings are produced`() = runTest {
        // Given
        val received = mutableListOf<VehicleTelemetry>()
        val job = launch { service().stream(fleet).collect { received += it } }
        advanceTimeBy(2 * INTERVAL_MS + 1)
        runCurrent()
        val countBeforeCancel = received.size

        // When
        job.cancelAndJoin()
        advanceTimeBy(10 * INTERVAL_MS)
        runCurrent()

        // Then
        assertThat(countBeforeCancel).isGreaterThan(0)
        assertThat(job.isCancelled).isTrue()
        assertThat(received).hasSize(countBeforeCancel)
    }

    @Test
    fun `given an empty fleet when stream then throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> { service().stream(emptyList()) }
    }

    @Test
    fun `given a zero or negative interval when creating the service then throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> { SimulatedTelemetryStreamingService(updateIntervalMs = 0L) }
        assertFailsWith<IllegalArgumentException> { SimulatedTelemetryStreamingService(updateIntervalMs = -1L) }
    }
}