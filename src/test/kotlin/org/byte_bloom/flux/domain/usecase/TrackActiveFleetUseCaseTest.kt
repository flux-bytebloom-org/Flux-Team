package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.byte_bloom.flux.domain.model.VehicleTelemetry
import org.byte_bloom.flux.domain.response.DelayNotification
import org.byte_bloom.flux.domain.telemetry.TelemetryStreamingService
import org.byte_bloom.flux.domain.testdata.aVehicle
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith


class TrackActiveFleetUseCaseTest {
    private companion object {
        const val SLOW_KMH = 10.0
        const val FAST_KMH = 60.0
        const val SPEED_AT_THRESHOLD_KMH = 20.0
        const val SPEED_JUST_BELOW_THRESHOLD_KMH = 19.9
        const val NORMAL_FUEL = 50.0
        const val NORMAL_LATITUDE = 31.9
        const val NORMAL_LONGITUDE = 35.2
        const val CONSECUTIVE_READINGS_FOR_DELAY = 3
    }

    private val service = mockk<TelemetryStreamingService>()
    private val useCase = TrackActiveFleetUseCase(service)
    private val vehicles = listOf(aVehicle(id = "V-1"), aVehicle(id = "V-2"))

    private fun reading(
        vehicleId: String = "V-1",
        speed: Double,
        fuel: Double = NORMAL_FUEL,
        latitude: Double = NORMAL_LATITUDE,
        longitude: Double = NORMAL_LONGITUDE,
        timestamp: Long = 0L
    ) = VehicleTelemetry(vehicleId, latitude, longitude, speed, fuel, timestamp)

    private fun givenReadings(vararg readings: VehicleTelemetry) {
        every { service.stream(vehicles) } returns flowOf(*readings)
    }

    private suspend fun trackedNotifications(): List<DelayNotification> = useCase(vehicles).toList()

    @Test
    fun `given three consecutive slow readings when track then emits one delay notification`() = runTest {
        // Given
        givenReadings(
            reading(speed = SLOW_KMH, timestamp = 1L),
            reading(speed = SLOW_KMH, timestamp = 2L),
            reading(speed = 8.0, latitude = 31.95, longitude = 35.25, timestamp = 3L)
        )

        // When
        val notifications = trackedNotifications()

        // Then
        assertThat(notifications).containsExactly(
            DelayNotification(
                vehicleId = "V-1",
                consecutiveSlowReadings = CONSECUTIVE_READINGS_FOR_DELAY,
                lastSpeedKmh = 8.0,
                latitude = 31.95,
                longitude = 35.25,
                timestamp = 3L
            )
        )
    }

    @Test
    fun `given only two slow readings when track then emits nothing`() = runTest {
        // Given
        givenReadings(reading(speed = SLOW_KMH), reading(speed = SLOW_KMH))

        // When
        val notifications = trackedNotifications()

        // Then
        assertThat(notifications).isEmpty()
    }

    @Test
    fun `given a long slow streak when track then emits only one notification`() = runTest {
        // Given
        givenReadings(*Array(5) { reading(speed = SLOW_KMH) })

        // When
        val notifications = trackedNotifications()

        // Then
        assertThat(notifications).hasSize(1)
    }

    @Test
    fun `given a normal reading in the middle when track then the slow counter resets`() = runTest {
        // Given
        givenReadings(
            reading(speed = SLOW_KMH),
            reading(speed = SLOW_KMH),
            reading(speed = FAST_KMH),
            reading(speed = SLOW_KMH),
            reading(speed = SLOW_KMH)
        )

        // When
        val notifications = trackedNotifications()

        // Then
        assertThat(notifications).isEmpty()
    }

    @Test
    fun `given two separate slow streaks when track then emits a notification for each`() = runTest {
        // Given
        givenReadings(
            reading(speed = SLOW_KMH), reading(speed = SLOW_KMH), reading(speed = SLOW_KMH),
            reading(speed = FAST_KMH),
            reading(speed = SLOW_KMH), reading(speed = SLOW_KMH), reading(speed = SLOW_KMH)
        )

        // When
        val notifications = trackedNotifications()

        // Then
        assertThat(notifications).hasSize(2)
    }

    @Test
    fun `given speed exactly at the threshold when track then it is not counted as slow`() = runTest {
        // Given
        givenReadings(*Array(3) { reading(speed = SPEED_AT_THRESHOLD_KMH) })

        // When
        val notifications = trackedNotifications()

        // Then
        assertThat(notifications).isEmpty()
    }

    @Test
    fun `given speed just below the threshold when track then it is counted as slow`() = runTest {
        // Given
        givenReadings(*Array(3) { reading(speed = SPEED_JUST_BELOW_THRESHOLD_KMH) })

        // When
        val notifications = trackedNotifications()

        // Then
        assertThat(notifications).hasSize(1)
    }

    @Test
    fun `given readings of two vehicles mixed together when track then each vehicle has its own counter`() = runTest {
        // Given: V-1 reaches three slow readings, V-2 is interrupted by a normal reading
        givenReadings(
            reading(vehicleId = "V-1", speed = SLOW_KMH),
            reading(vehicleId = "V-2", speed = SLOW_KMH),
            reading(vehicleId = "V-1", speed = SLOW_KMH),
            reading(vehicleId = "V-2", speed = FAST_KMH),
            reading(vehicleId = "V-1", speed = SLOW_KMH),
            reading(vehicleId = "V-2", speed = SLOW_KMH)
        )

        // When
        val notifications = trackedNotifications()

        // Then
        assertThat(notifications.map { it.vehicleId }).containsExactly("V-1")
    }

    @Test
    fun `given slow readings with invalid data when track then invalid readings are dropped`() = runTest {
        // Given: all of them look slow but each one has an impossible value
        givenReadings(
            reading(speed = SLOW_KMH, fuel = 150.0),
            reading(speed = SLOW_KMH, fuel = -1.0),
            reading(speed = SLOW_KMH, latitude = 95.0)
        )

        // When
        val notifications = trackedNotifications()

        // Then
        assertThat(notifications).isEmpty()
    }

    @Test
    fun `given negative or not a number speed when track then invalid readings are dropped`() = runTest {
        // Given
        givenReadings(
            reading(speed = -5.0),
            reading(speed = Double.NaN),
            reading(speed = -1.0)
        )

        // When
        val notifications = trackedNotifications()

        // Then
        assertThat(notifications).isEmpty()
    }

    @Test
    fun `given an invalid reading between slow readings when track then it neither counts nor resets`() = runTest {
        // Given
        givenReadings(
            reading(speed = SLOW_KMH),
            reading(speed = SLOW_KMH),
            reading(speed = FAST_KMH, fuel = 150.0),
            reading(speed = SLOW_KMH)
        )

        // When
        val notifications = trackedNotifications()

        // Then
        assertThat(notifications).hasSize(1)
    }

    @Test
    fun `given an empty telemetry stream when track then emits nothing`() = runTest {
        // Given
        givenReadings()

        // When
        val notifications = trackedNotifications()

        // Then
        assertThat(notifications).isEmpty()
    }

    @Test
    fun `given the telemetry service fails when track then the exception reaches the collector`() = runTest {
        // Given
        every { service.stream(vehicles) } returns
                flow<VehicleTelemetry> { throw IllegalStateException("telemetry down") }

        // When
        val exception = assertFailsWith<IllegalStateException> { trackedNotifications() }

        // Then
        assertThat(exception).hasMessageThat().isEqualTo("telemetry down")
    }

    @Test
    fun `given a tracking flow when it is not collected then the service is not touched`() = runTest {
        // Given
        givenReadings(reading(speed = SLOW_KMH))

        // When
        val notificationsFlow = useCase(vehicles)

        // Then
        verify(exactly = 0) { service.stream(any()) }
        notificationsFlow.toList()
        verify(exactly = 1) { service.stream(vehicles) }
    }

    @Test
    fun `given the same flow collected twice when track then each collection has its own counters`() = runTest {
        // Given
        givenReadings(*Array(3) { reading(speed = SLOW_KMH) })
        val notificationsFlow = useCase(vehicles)

        // When
        val first = notificationsFlow.toList()
        val second = notificationsFlow.toList()

        // Then
        assertThat(first).hasSize(1)
        assertThat(second).hasSize(1)
    }
}