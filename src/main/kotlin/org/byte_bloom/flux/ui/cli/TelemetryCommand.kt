package org.byte_bloom.flux.ui.cli

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.byte_bloom.flux.domain.usecase.TrackActiveFleetUseCase
import org.byte_bloom.flux.ui.utils.NetworkLoader
import org.byte_bloom.flux.ui.utils.toDisplayLine

private const val START_LABEL = "Start telemetry logs"
private const val STOP_LABEL = "Stop telemetry logs"

/**
 * Toggles live delay alerts. The stream runs in [scope] (the application scope),
 * so the menu stays usable while alerts keep printing, and closing the application
 * cancels it together with everything else.
 */
class TelemetryCommand(
    private val terminal: Terminal,
    private val networkLoader: NetworkLoader,
    private val trackActiveFleet: TrackActiveFleetUseCase,
    private val scope: CoroutineScope
) : CliCommand {

    private var telemetryJob: Job? = null
    private val isRunning: Boolean get() = telemetryJob?.isActive == true

    // The menu is printed on every loop, so the label always shows the current action.
    override val label: String
        get() = if (isRunning) STOP_LABEL else START_LABEL

    override suspend fun execute() {
        if (isRunning) stop() else start()
    }

    private suspend fun start() {
        val fleet = networkLoader.load().fleet
        if (fleet.isEmpty()) {
            terminal.writeLine("No vehicles available to track")
            return
        }

        printDescription()
        telemetryJob = scope.launch {
            trackActiveFleet(fleet)
                .catch { error -> terminal.writeLine("Telemetry error: ${error.message}") }
                .collect { alert -> terminal.writeLine(" ===>"+alert.toDisplayLine()) }
        }
        terminal.writeLine("Telemetry started. Delay alerts will appear here.")
    }

    private suspend fun stop() {
        telemetryJob?.cancelAndJoin()
        terminal.writeLine("Telemetry stopped")
    }
    private fun printDescription() {
        terminal.writeLine("Streams live vehicle readings in the background.")
        terminal.writeLine("A delay alert appears when a vehicle keeps moving slowly for several readings in a row.")
        terminal.writeLine("Choose this option again to stop.")
    }
}
