package org.byte_bloom.flux.data.remote.resilience.circuitbreaker

enum class CircuitBreakerState {
    CLOSED, OPEN, HALF_OPEN
}
