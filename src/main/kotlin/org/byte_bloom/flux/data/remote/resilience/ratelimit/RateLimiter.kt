package org.byte_bloom.flux.data.remote.resilience.ratelimit

import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min

class RateLimiter(
    private val maxBucketCapacity: Int,
    private val refillRatePerSecond: Double,
    private val nanoTime: () -> Long = System::nanoTime
) {
    init {
        require(maxBucketCapacity > 0) { "maxBucketCapacity must be positive" }
        require(refillRatePerSecond > 0.0) { "refillRatePerSecond must be positive" }
    }

    private val tokens = AtomicInteger(maxBucketCapacity)
    private val lastRefillNanos = AtomicLong(nanoTime())

    fun tryAcquire(): Boolean {
        refill()
        while (true) {
            val current = tokens.get()
            if (current <= 0) return false
            if (tokens.compareAndSet(current, current - 1)) return true
        }
    }

    suspend fun acquire() {
        while (!tryAcquire()) {
            delay(nanosUntilNextToken() / NANOS_PER_MILLI)
        }
    }

    private fun refill() {
        val now = nanoTime()
        val last = lastRefillNanos.get()
        val elapsed = now - last
        if (elapsed <= 0L) return

        val tokensToAdd = (elapsed * refillRatePerSecond / NANOS_PER_SECOND).toInt()
        if (tokensToAdd <= 0) return

        if (!lastRefillNanos.compareAndSet(last, now)) return

        while (true) {
            val current = tokens.get()
            val updated = min(maxBucketCapacity, current + tokensToAdd)
            if (tokens.compareAndSet(current, updated)) return
        }
    }

    private fun nanosUntilNextToken(): Long {
        val ratePerNano = refillRatePerSecond / NANOS_PER_SECOND
        if (ratePerNano <= 0.0) return NANOS_PER_MILLI
        val nanos = (1.0 / ratePerNano).toLong()
        return if (nanos < MIN_WAIT_NANOS) MIN_WAIT_NANOS else nanos
    }

    private companion object {
        const val NANOS_PER_SECOND = 1_000_000_000.0
        const val NANOS_PER_MILLI = 1_000_000L
        const val MIN_WAIT_NANOS = 1_000_000L
    }

}