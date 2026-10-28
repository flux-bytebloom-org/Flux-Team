package org.byte_bloom.flux.data.remote.middleware

import org.byte_bloom.flux.data.remote.resilience.circuitbreaker.CircuitBreaker

class CircuitBreakerMiddleware(
    private val circuitBreaker: CircuitBreaker
) : ResilienceMiddleware {
    override suspend fun <T> intercept(next: suspend () -> T): T =
        circuitBreaker.execute { next() }
}
