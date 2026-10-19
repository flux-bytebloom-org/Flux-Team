package org.byte_bloom.flux.ui.scenarios

import kotlinx.coroutines.withTimeoutOrNull
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.usecase.TrackActiveFleetUseCase
import org.byte_bloom.flux.ui.utils.toDisplayLine
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

suspend fun runTelemetryScenario(vehicles: List<Vehicle>, duration: Duration = 10.seconds) {
    val trackActiveFleet: TrackActiveFleetUseCase = getKoin().get()

    println("\n--- Real-Time Fleet Telemetry ($duration demo) ---")

    withTimeoutOrNull(duration) {
        trackActiveFleet(vehicles).collect { alert ->
            println( alert.toDisplayLine())
        }
    }

    println("Telemetry monitoring stopped.")
}
