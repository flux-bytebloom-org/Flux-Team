package org.byte_bloom.flux.ui.cli

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SPINNER_FRAMES = "|/-\\"
private const val FRAME_INTERVAL_MILLIS = 100L
private const val CARRIAGE_RETURN = "\r"
private const val FRAME_PREFIX_LENGTH = 2

class LoadingIndicator(private val terminal: Terminal) {

    suspend fun <T> whileLoading(message: String, task: suspend () -> T): T = coroutineScope {
        val spinner = launch { spin(message) }
        try {
            task()
        } finally {
            // NonCancellable: the spinner must stop even when the task itself was cancelled.
            withContext(NonCancellable) { spinner.cancelAndJoin() }
            clearLine(message)
        }
    }

    private suspend fun spin(message: String) {
        var frameIndex = 0
        while (currentCoroutineContext().isActive) {
            val frame = SPINNER_FRAMES[frameIndex % SPINNER_FRAMES.length]
            terminal.write("$CARRIAGE_RETURN$frame $message")
            frameIndex++
            delay(FRAME_INTERVAL_MILLIS)
        }
    }

    private fun clearLine(message: String) {
        val blankLine = " ".repeat(message.length + FRAME_PREFIX_LENGTH)
        terminal.write("$CARRIAGE_RETURN$blankLine$CARRIAGE_RETURN")
    }
}
