package org.byte_bloom.flux.ui.cli

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

private const val MESSAGE = "Loading"
private const val TASK_DURATION_MILLIS = 250L
private const val LONG_WAIT_MILLIS = 5_000L
private const val TASK_RESULT = 42

class LoadingIndicatorTest {

    private val terminal = FakeTerminal()
    private val indicator = LoadingIndicator(terminal)

    @Test
    fun `given a successful task when whileLoading then returns its result`() = runTest {
        val result = indicator.whileLoading(MESSAGE) {
            delay(TASK_DURATION_MILLIS)
            TASK_RESULT
        }

        assertThat(result).isEqualTo(TASK_RESULT)
    }

    @Test
    fun `given a slow task when whileLoading then the spinner animates next to the message`() = runTest {
        indicator.whileLoading(MESSAGE) { delay(TASK_DURATION_MILLIS) }

        assertThat(terminal.output()).contains("| $MESSAGE")
        assertThat(terminal.output()).contains("/ $MESSAGE")
    }

    @Test
    fun `given a successful task when whileLoading then the spinner stops and the line is cleared`() = runTest {
        indicator.whileLoading(MESSAGE) { delay(TASK_DURATION_MILLIS) }

        assertSpinnerStoppedAndLineCleared()
    }

    @Test
    fun `given a failing task when whileLoading then the error propagates and the spinner stops`() = runTest {
        assertFailsWith<IllegalStateException> {
            indicator.whileLoading(MESSAGE) {
                delay(TASK_DURATION_MILLIS)
                error("boom")
            }
        }

        assertSpinnerStoppedAndLineCleared()
    }

    @Test
    fun `given the caller is cancelled when whileLoading then the spinner stops and the line is cleared`() = runTest {
        val job = launch {
            indicator.whileLoading(MESSAGE) { delay(Long.MAX_VALUE) }
        }
        advanceTimeBy(TASK_DURATION_MILLIS)

        job.cancelAndJoin()

        assertSpinnerStoppedAndLineCleared()
    }

    // Nothing may be written after the loading call ends, and the last thing written is a blank line.
    private fun TestScope.assertSpinnerStoppedAndLineCleared() {
        val lengthWhenFinished = terminal.output().length

        advanceTimeBy(LONG_WAIT_MILLIS)

        assertThat(terminal.output().length).isEqualTo(lengthWhenFinished)
        val clearedSection = terminal.output().substringBeforeLast("\r").substringAfterLast("\r")
        assertThat(clearedSection).isNotEmpty()
        assertThat(clearedSection.isBlank()).isTrue()
    }
}
