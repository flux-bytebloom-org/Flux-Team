package org.byte_bloom.flux.data.remote.ratelimit

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RateLimiterTest {

    @Test
    fun `tryAcquire succeeds until capacity then fails`() {
        val limiter = RateLimiter(
            maxBucketCapacity = 3,
            refillRatePerSecond = 1000.0,
            nanoTime = { 0L }
        )

        assertTrue(limiter.tryAcquire())
        assertTrue(limiter.tryAcquire())
        assertTrue(limiter.tryAcquire())
        assertFalse(limiter.tryAcquire())
    }

    @Test
    fun `tokens refill based on elapsed time`() {
        var now = 0L
        val limiter = RateLimiter(
            maxBucketCapacity = 2,
            refillRatePerSecond = 10.0,
            nanoTime = { now }
        )

        assertTrue(limiter.tryAcquire())
        assertTrue(limiter.tryAcquire())
        assertFalse(limiter.tryAcquire())

        // 0.1 second at 10 tokens/sec → +1 token
        now += 100_000_000L

        assertTrue(limiter.tryAcquire())
        assertFalse(limiter.tryAcquire())
    }

    @Test
    fun `refill does not exceed max capacity`() {
        var now = 0L
        val limiter = RateLimiter(
            maxBucketCapacity = 2,
            refillRatePerSecond = 100.0,
            nanoTime = { now }
        )

        assertTrue(limiter.tryAcquire())
        assertTrue(limiter.tryAcquire())

        // long wait would add many tokens, but capacity is 2
        now += 5_000_000_000L

        assertTrue(limiter.tryAcquire())
        assertTrue(limiter.tryAcquire())
        assertFalse(limiter.tryAcquire())
    }

    @Test
    fun `acquire waits until a token is available`() = runBlocking {
        var now = 0L
        val limiter = RateLimiter(
            maxBucketCapacity = 1,
            refillRatePerSecond = 5.0,
            nanoTime = { now }
        )

        assertTrue(limiter.tryAcquire())
        assertFalse(limiter.tryAcquire())

        now += 300_000_000L // enough for at least 1 token at 5/sec

        limiter.acquire()
    }

    @Test
    fun `invalid capacity is rejected`() {
        assertThrows<IllegalArgumentException> {
            RateLimiter(maxBucketCapacity = 0, refillRatePerSecond = 1.0)
        }
    }

    @Test
    fun `invalid refill rate is rejected`() {
        assertThrows<IllegalArgumentException> {
            RateLimiter(maxBucketCapacity = 1, refillRatePerSecond = 0.0)
        }
    }
}