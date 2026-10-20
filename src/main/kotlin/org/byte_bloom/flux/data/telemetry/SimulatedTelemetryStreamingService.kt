package org.byte_bloom.flux.data.telemetry

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.VehicleTelemetry
import org.byte_bloom.flux.domain.telemetry.TelemetryStreamingService
import kotlin.random.Random

class SimulatedTelemetryStreamingService(
    private val updateIntervalMs: Long = DEFAULT_UPDATE_INTERVAL_MS,
    private val random: Random = Random.Default,
    private val timestampProvider: () -> Long = System::currentTimeMillis
) : TelemetryStreamingService {

    init {
        require(updateIntervalMs > 0) { "Update interval must be greater than zero" }
    }

    override fun stream(vehicles: List<Vehicle>): Flow<VehicleTelemetry> {
        require(vehicles.isNotEmpty()) { "At least one vehicle is required" }

        val startingFuelPercent = 100.0

        return flow {
            val states = vehicles.associate { vehicle ->
                vehicle.id to VehicleState(
                    latitude = vehicle.currentHub.latitude,
                    longitude = vehicle.currentHub.longitude,
                    fuelPercent = startingFuelPercent
                )
            }

            while (true) {
                vehicles.forEach { vehicle ->
                    emit(nextReading(vehicle.id, states.getValue(vehicle.id)))
                }
                delay(updateIntervalMs)
            }
        }
    }

    private class VehicleState(
        var latitude: Double,
        var longitude: Double,
        var fuelPercent: Double
    )

    private fun nextReading(vehicleId: String, state: VehicleState): VehicleTelemetry {
        val minLatitude = -90.0
        val maxLatitude = 90.0
        val minLongitude = -180.0
        val maxLongitude = 180.0
        val maxFuelBurnPerUpdate = 0.5

        state.latitude = (state.latitude + nextDrift()).coerceIn(minLatitude, maxLatitude)
        state.longitude = (state.longitude + nextDrift()).coerceIn(minLongitude, maxLongitude)
        state.fuelPercent =
            (state.fuelPercent - random.nextDouble(0.0, maxFuelBurnPerUpdate)).coerceAtLeast(0.0)

        return VehicleTelemetry(
            vehicleId = vehicleId,
            latitude = state.latitude,
            longitude = state.longitude,
            speedKmh = nextSpeed(),
            fuelPercentage = state.fuelPercent,
            timestamp = timestampProvider()
        )
    }

    private fun nextDrift(): Double {
        val maxDriftDegrees = 0.001
        return random.nextDouble(-maxDriftDegrees, maxDriftDegrees)
    }

    private fun nextSpeed(): Double {
        val congestionProbability = 0.3
        val congestedMaxKmh = 15.0
        val cruisingMinKmh = 40.0
        val cruisingMaxKmh = 90.0

        return if (random.nextDouble() < congestionProbability) {
            random.nextDouble(0.0, congestedMaxKmh)
        } else {
            random.nextDouble(cruisingMinKmh, cruisingMaxKmh)
        }
    }

    private companion object {
        const val DEFAULT_UPDATE_INTERVAL_MS = 1_000L
    }
}