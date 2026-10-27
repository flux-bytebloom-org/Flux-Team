package org.byte_bloom.flux.domain.algorithm.circuitbreaker

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.exception.LogisticsException.DataAccessException.CallNotPermittedException
import org.byte_bloom.flux.domain.exception.LogisticsException.DataAccessException.RemoteHttpException
import org.junit.jupiter.api.Test

private const val FAILURES_TO_TRIP = 5
private const val OPEN_MS = 5_000L
private const val NANOS_PER_MILLI = 1_000_000L
private const val SERVER_ERROR = 503
private const val UNPROCESSABLE = 422
private const val PARALLEL_CALLS = 50

private class FakeClock : NanoClock {
    private var nowNanos = 0L
    override fun nanoTime(): Long = nowNanos
    fun advanceMs(ms: Long) { nowNanos += ms * NANOS_PER_MILLI }
}

private fun transientError() = RemoteHttpException(statusCode = SERVER_ERROR, reason = "down")
private fun nonTransientError() = RemoteHttpException(statusCode = UNPROCESSABLE, reason = "invalid")

private suspend fun CircuitBreaker.failWith(error: Throwable): Throwable? =
    runCatching { execute<Unit> { throw error } }.exceptionOrNull()

private suspend fun CircuitBreaker.trip() = repeat(FAILURES_TO_TRIP) { failWith(transientError()) }

@OptIn(ExperimentalCoroutinesApi::class)
class CircuitBreakerTest {

    private val clock = FakeClock()
    private val breaker = CircuitBreaker(clock)

    private suspend fun assertRejected() {
        val error = runCatching { breaker.execute { "x" } }.exceptionOrNull()
        assertThat(error).isInstanceOf(CallNotPermittedException::class.java)
    }

    // ---------- CLOSED ----------

    @Test
    fun `closed circuit lets calls through and returns the value`() = runTest {
        // given a fresh breaker
        // when
        val result = breaker.execute { "ok" }
        // then
        assertThat(result).isEqualTo("ok")
    }

    @Test
    fun `five consecutive transient failures open the circuit and the next call never runs`() = runTest {
        // given
        breaker.trip()
        var executed = false
        // when
        val error = runCatching { breaker.execute { executed = true } }.exceptionOrNull()
        // then
        assertThat(error).isInstanceOf(CallNotPermittedException::class.java)
        assertThat(executed).isFalse()
    }

    @Test
    fun `a success in between resets the consecutive failure count`() = runTest {
        repeat(FAILURES_TO_TRIP - 1) { breaker.failWith(transientError()) }
        breaker.execute { "ok" }
        repeat(FAILURES_TO_TRIP - 1) { breaker.failWith(transientError()) }

        assertThat(breaker.execute { "still closed" }).isEqualTo("still closed")
    }

    @Test
    fun `non transient failures are rethrown as is and never open the circuit`() = runTest {
        repeat(FAILURES_TO_TRIP * 2) {
            val error = breaker.failWith(nonTransientError())
            assertThat(error).isInstanceOf(RemoteHttpException::class.java)
            assertThat((error as RemoteHttpException).statusCode).isEqualTo(UNPROCESSABLE)
        }

        assertThat(breaker.execute { "ok" }).isEqualTo("ok")
    }

    @Test
    fun `a non transient failure neither resets nor adds to the failure count`() = runTest {
        repeat(FAILURES_TO_TRIP - 1) { breaker.failWith(transientError()) }
        breaker.failWith(nonTransientError())
        breaker.failWith(transientError())

        assertRejected()
    }

    // ---------- OPEN ----------

    @Test
    fun `open circuit rejects before the open period ends and reports the remaining time`() = runTest {
        breaker.trip()
        clock.advanceMs(OPEN_MS - 1)

        val error = runCatching { breaker.execute { "x" } }.exceptionOrNull()

        assertThat(error).isInstanceOf(CallNotPermittedException::class.java)
        assertThat((error as CallNotPermittedException).retryAfterMs).isEqualTo(1L)
    }

    // ---------- HALF_OPEN ----------

    @Test
    fun `after the open period one probe runs and its success closes the circuit`() = runTest {
        breaker.trip()
        clock.advanceMs(OPEN_MS)

        assertThat(breaker.execute { "probe" }).isEqualTo("probe")
        assertThat(breaker.execute { "normal" }).isEqualTo("normal")
    }

    @Test
    fun `a successful probe resets the failure counter`() = runTest {
        breaker.trip()
        clock.advanceMs(OPEN_MS)
        breaker.execute { "probe" }

        repeat(FAILURES_TO_TRIP - 1) { breaker.failWith(transientError()) }

        assertThat(breaker.execute { "still closed" }).isEqualTo("still closed")
    }

    @Test
    fun `a failed probe opens the circuit again for another full period`() = runTest {
        breaker.trip()
        clock.advanceMs(OPEN_MS)

        breaker.failWith(transientError())   // the probe fails
        assertRejected()

        clock.advanceMs(OPEN_MS - 1)
        assertRejected()

        clock.advanceMs(1)
        assertThat(breaker.execute { "probe again" }).isEqualTo("probe again")
    }

    @Test
    fun `only one probe gets through when many calls arrive after the open period`() = runTest {
        breaker.trip()
        clock.advanceMs(OPEN_MS)
        val gate = CompletableDeferred<Unit>()
        var executedCount = 0

        val calls = List(PARALLEL_CALLS) {
            async { runCatching { breaker.execute { executedCount++; gate.await(); "probe" } } }
        }
        runCurrent()
        assertThat(executedCount).isEqualTo(1)

        gate.complete(Unit)
        val results = calls.map { it.await() }

        assertThat(results.count { it.isSuccess }).isEqualTo(1)
        assertThat(results.count { it.exceptionOrNull() is CallNotPermittedException })
            .isEqualTo(PARALLEL_CALLS - 1)
    }

    // ---------- epoch + cancellation ----------

    @Test
    fun `a late result from a call that started before the circuit opened is ignored`() = runTest {
        val lateGate = CompletableDeferred<Unit>()
        val probeGate = CompletableDeferred<Unit>()

        val lateCall = async {
            runCatching { breaker.execute<Unit> { lateGate.await(); throw transientError() } }
        }
        runCurrent()                       // entered while CLOSED, now waiting

        breaker.trip()                     // circuit opens
        clock.advanceMs(OPEN_MS)
        val probe = async { runCatching { breaker.execute { probeGate.await(); "probe" } } }
        runCurrent()                       // HALF_OPEN, probe running

        lateGate.complete(Unit)            // the old call fails now: must be ignored
        runCurrent()
        probeGate.complete(Unit)
        probe.await()
        lateCall.await()

        assertThat(breaker.execute { "closed again" }).isEqualTo("closed again")
    }

    @Test
    fun `a cancelled probe does not leave the circuit stuck`() = runTest {
        breaker.trip()
        clock.advanceMs(OPEN_MS)

        val probe = launch { breaker.execute { CompletableDeferred<Unit>().await() } }
        runCurrent()
        probe.cancelAndJoin()

        assertThat(breaker.execute { "next probe" }).isEqualTo("next probe")
    }
}
