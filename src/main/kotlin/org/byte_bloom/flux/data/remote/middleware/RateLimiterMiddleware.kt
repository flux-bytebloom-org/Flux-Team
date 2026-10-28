package org.byte_bloom.flux.data.remote.middleware
import org.byte_bloom.flux.data.remote.resilience.ratelimit.RateLimiter

class RateLimiterMiddleware(
        private val rateLimiter: RateLimiter
    ) : ResilienceMiddleware {
        override suspend fun <T> intercept(next: suspend () -> T): T {
            rateLimiter.acquire()
            return next()
        }
    }
