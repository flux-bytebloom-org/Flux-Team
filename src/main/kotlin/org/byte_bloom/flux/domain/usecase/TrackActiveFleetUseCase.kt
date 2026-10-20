package org.byte_bloom.flux.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.VehicleTelemetry
import org.byte_bloom.flux.domain.response.DelayNotification
import org.byte_bloom.flux.domain.telemetry.TelemetryStreamingService

class TrackActiveFleetUseCase(
    private val telemetryService: TelemetryStreamingService
) {

    operator fun invoke(vehicles: List<Vehicle>): Flow<DelayNotification> = flow {
        val slowReadingsByVehicle = mutableMapOf<String, Int>()

        telemetryService.stream(vehicles)
            .filter { isValid(it) }
            .collect { telemetry ->
                val previousCount = slowReadingsByVehicle[telemetry.vehicleId] ?: NO_SLOW_READINGS
                val slowReadings = countSlowReadings(telemetry, previousCount)
                slowReadingsByVehicle[telemetry.vehicleId] = slowReadings

                if (slowReadings == SLOW_READINGS_BEFORE_DELAY) {
                    emit(telemetry.toDelayNotification(slowReadings))
                }
            }
    }

    private fun isValid(telemetry: VehicleTelemetry): Boolean {
        val minSpeedKmh = 0.0
        val minFuelPercent = 0.0
        val maxFuelPercent = 100.0
        val maxLatitude = 90.0
        val maxLongitude = 180.0

        return telemetry.speedKmh.isFinite() &&
                telemetry.speedKmh >= minSpeedKmh &&
                telemetry.fuelPercentage in minFuelPercent..maxFuelPercent &&
                telemetry.latitude in -maxLatitude..maxLatitude &&
                telemetry.longitude in -maxLongitude..maxLongitude
    }

    private fun countSlowReadings(telemetry: VehicleTelemetry, previousCount: Int): Int =
        if (telemetry.speedKmh < SLOW_SPEED_THRESHOLD_KMH) previousCount + 1 else NO_SLOW_READINGS

    private fun VehicleTelemetry.toDelayNotification(slowReadings: Int) = DelayNotification(
        vehicleId = vehicleId,
        consecutiveSlowReadings = slowReadings,
        lastSpeedKmh = speedKmh,
        latitude = latitude,
        longitude = longitude,
        timestamp = timestamp
    )

    private companion object {
        const val SLOW_SPEED_THRESHOLD_KMH = 20.0
        const val SLOW_READINGS_BEFORE_DELAY = 3
        const val NO_SLOW_READINGS = 0
    }
}