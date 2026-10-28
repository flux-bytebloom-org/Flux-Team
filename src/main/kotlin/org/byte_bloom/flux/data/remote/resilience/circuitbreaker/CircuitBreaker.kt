package org.byte_bloom.flux.data.remote.resilience.circuitbreaker

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.byte_bloom.flux.data.remote.resilience.NanoClock
import org.byte_bloom.flux.data.remote.resilience.SystemNanoClock
import org.byte_bloom.flux.domain.exception.LogisticsException.DataAccessException.CallNotPermittedException
import org.byte_bloom.flux.domain.exception.isTransient

private const val FAILURE_THRESHOLD = 5
private const val OPEN_DURATION_MS = 5_000L
private const val NANOS_PER_MILLI = 1_000_000L
private const val OPEN_DURATION_NANOS = OPEN_DURATION_MS * NANOS_PER_MILLI

class CircuitBreaker(
    private val clock: NanoClock = SystemNanoClock
) {
    private val mutex = Mutex()

    private var state = CircuitBreakerState.CLOSED
    private var consecutiveFailures = 0
    private var openedAtNanos = 0L
    private var epoch = 0L

    suspend fun <T> execute(block: suspend () -> T): T {
        val callEpoch = mutex.withLock { beforeCall() }

        val value = try {
            block()
        } catch (e: Exception) {
            record(callEpoch, classify(e))
            throw e
        }

        record(callEpoch, CallOutcome.SUCCESS)
        return value
    }

    // Actions across States (implement State digram)

    private fun beforeCall(): Long = when (state) {
        CircuitBreakerState.CLOSED -> epoch
        CircuitBreakerState.OPEN -> allowProbeOrReject()
        CircuitBreakerState.HALF_OPEN -> throw CallNotPermittedException()
    }

    private fun onSuccess(callEpoch: Long) {
        if (isStale(callEpoch)) return
        when (state) {
            CircuitBreakerState.CLOSED -> consecutiveFailures = 0
            CircuitBreakerState.HALF_OPEN -> closeCircuit()
            CircuitBreakerState.OPEN -> Unit
        }
    }

    private fun onTransientFailure(callEpoch: Long) {
        if (isStale(callEpoch)) return
        when (state) {
            CircuitBreakerState.CLOSED -> countFailure()
            CircuitBreakerState.HALF_OPEN -> openCircuit()
            CircuitBreakerState.OPEN -> Unit
        }
    }

    private fun onNeutral(callEpoch: Long) {
        if (isStale(callEpoch)) return
        if (state == CircuitBreakerState.HALF_OPEN) releaseProbe()
    }

    // States helpers

    private fun allowProbeOrReject(): Long {
        val elapsedNanos = clock.nanoTime() - openedAtNanos
        if (elapsedNanos < OPEN_DURATION_NANOS) {
            val remainingMs = (OPEN_DURATION_NANOS - elapsedNanos) / NANOS_PER_MILLI
            throw CallNotPermittedException(retryAfterMs = remainingMs)
        }
        transitionTo(CircuitBreakerState.HALF_OPEN)
        return epoch
    }

    private fun transitionTo(newState: CircuitBreakerState) {
        println("[CIRCUIT] $state -> $newState")
        state = newState
        epoch++
    }

    private fun isStale(callEpoch: Long): Boolean = callEpoch != epoch

    private fun countFailure() {
        consecutiveFailures++
        if (consecutiveFailures >= FAILURE_THRESHOLD) openCircuit()
    }

    private fun openCircuit() {
        openedAtNanos = clock.nanoTime()
        transitionTo(CircuitBreakerState.OPEN)
    }

    private fun closeCircuit() {
        consecutiveFailures = 0
        transitionTo(CircuitBreakerState.CLOSED)
    }

    private fun releaseProbe() {
        transitionTo(CircuitBreakerState.OPEN)
    }

    // execute fun's helpers
    private fun classify(error: Exception): CallOutcome =
        if (isTransient(error)) CallOutcome.TRANSIENT_FAILURE else CallOutcome.NEUTRAL

    private suspend fun record(callEpoch: Long, outcome: CallOutcome) =
        withContext(NonCancellable) {
            mutex.withLock { applyOutcome(callEpoch, outcome) }
        }

    private fun applyOutcome(callEpoch: Long, outcome: CallOutcome) = when (outcome) {
        CallOutcome.SUCCESS -> onSuccess(callEpoch)
        CallOutcome.TRANSIENT_FAILURE -> onTransientFailure(callEpoch)
        CallOutcome.NEUTRAL -> onNeutral(callEpoch)
    }


    private enum class CallOutcome { SUCCESS, TRANSIENT_FAILURE, NEUTRAL }
}
