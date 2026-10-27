package org.byte_bloom.flux.data.remote.retry

import kotlinx.coroutines.test.runTest
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

class ExponentialBackoffRetryTest {

    private fun httpError(status: Int) =
        LogisticsException.DataAccessException.RemoteHttpException(statusCode = status, reason = "error $status")

    private fun runWithFailure(failure: Exception, delays: MutableList<Long> = mutableListOf()): Pair<Result<String>, Int> {
        var attempts = 0
        val result = retryWithBackoffInternal(
            maxRetries = 3,
            initialDelayMs = 1000,
            factor = 2.0,
            wait = { delays.add(it) },
            block = {
                attempts++
                throw failure
            }
        )
        return result to attempts
    }

    @Test
    fun `returns success on first attempt`() {
        var attempts = 0

        val result = retryWithBackoffInternal(3, 1000, 2.0, wait = {}, block = { attempts++; "ok" })

        assertTrue(result.isSuccess)
        assertEquals("ok", result.getOrThrow())
        assertEquals(1, attempts)
    }

    @Test
    fun `retries transient failures with exponential delays then succeeds`() {
        var attempts = 0
        val delays = mutableListOf<Long>()

        val result = retryWithBackoffInternal(3, 1000, 2.0, wait = { delays.add(it) }) {
            attempts++
            if (attempts < 4) throw SocketTimeoutException("timeout")
            "ok"
        }

        assertTrue(result.isSuccess)
        assertEquals(4, attempts)
        assertEquals(listOf(1000L, 2000L, 4000L), delays)
    }

    @Test
    fun `gives up after max retries and returns the last failure`() {
        val delays = mutableListOf<Long>()

        val (result, attempts) = runWithFailure(SocketTimeoutException("timeout"), delays)

        assertTrue(result.isFailure)
        assertEquals(4, attempts)
        assertEquals(listOf(1000L, 2000L, 4000L), delays)
        assertTrue(result.exceptionOrNull() is SocketTimeoutException)
    }

    @Test
    fun `retries server errors and rate limiting`() {
        listOf(408, 429, 500, 503).forEach { status ->
            val (_, attempts) = runWithFailure(httpError(status))
            assertEquals(4, attempts, "status $status should be retried")
        }
    }

    @Test
    fun `does not retry unauthorized or unprocessable responses`() {
        listOf(401, 422).forEach { status ->
            val delays = mutableListOf<Long>()
            val (result, attempts) = runWithFailure(httpError(status), delays)

            assertTrue(result.isFailure)
            assertEquals(1, attempts, "status $status must not be retried")
            assertTrue(delays.isEmpty())
        }
    }

    @Test
    fun `does not retry validation failure`() {
        val (result, attempts) = runWithFailure(
            LogisticsException.ValidationException.EntityValidationException(listOf("Invalid package"))
        )

        assertTrue(result.isFailure)
        assertEquals(1, attempts)
    }

    @Test
    fun `does not retry when circuit breaker is open`() {
        val (_, attempts) = runWithFailure(
            LogisticsException.DataAccessException.CallNotPermittedException()
        )

        assertEquals(1, attempts)
    }

    @Test
    fun `propagates cancellation without retrying`() {
        var attempts = 0

        assertThrows(CancellationException::class.java) {
            retryWithBackoffInternal(3, 1000, 2.0, wait = {}) {
                attempts++
                throw CancellationException("cancelled")
            }
        }

        assertEquals(1, attempts)
    }

    @Test
    fun `maxRetries zero makes a single attempt`() {
        val delays = mutableListOf<Long>()
        var attempts = 0

        val result = retryWithBackoffInternal(0, 1000, 2.0, wait = { delays.add(it) }) {
            attempts++
            throw SocketTimeoutException("timeout")
        }

        assertTrue(result.isFailure)
        assertEquals(1, attempts)
        assertTrue(delays.isEmpty())
    }

    @Test
    fun `rejects invalid arguments`() {
        assertThrows(IllegalArgumentException::class.java) { retryWithBackoff(maxRetries = -1) { "x" } }
        assertThrows(IllegalArgumentException::class.java) { retryWithBackoff(initialDelayMs = -1) { "x" } }
        assertThrows(IllegalArgumentException::class.java) { retryWithBackoff(factor = 0.5) { "x" } }
    }

    @Test
    fun `public function keeps the required signature and works with zero delay`() {
        var attempts = 0

        val result = retryWithBackoff(maxRetries = 2, initialDelayMs = 0) {
            attempts++
            if (attempts < 3) throw SocketTimeoutException("timeout")
            "ok"
        }

        assertEquals("ok", result.getOrThrow())
        assertEquals(3, attempts)
    }

    @Test
    fun `suspend retry uses the same exponential delays`() = runTest {
        var attempts = 0
        val delays = mutableListOf<Long>()

        val result = retrySuspendWithBackoffInternal(2, 100, 2.0, wait = { delays.add(it) }) {
            attempts++
            if (attempts < 3) throw SocketTimeoutException("timeout")
            "ok"
        }

        assertTrue(result.isSuccess)
        assertEquals(3, attempts)
        assertEquals(listOf(100L, 200L), delays)
    }
}