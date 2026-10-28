package org.byte_bloom.flux.data.remote.middleware

interface ResilienceMiddleware {
    suspend fun <T> intercept(next: suspend () -> T): T
}
