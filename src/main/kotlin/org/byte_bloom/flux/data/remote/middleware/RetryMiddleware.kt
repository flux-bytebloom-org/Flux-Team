package org.byte_bloom.flux.data.remote.middleware

import org.byte_bloom.flux.data.remote.retry.retrySuspendWithBackoff

class RetryMiddleware : ResilienceMiddleware {

    override suspend fun <T> intercept(next: suspend () -> T): T =
        retrySuspendWithBackoff { next() }.getOrThrow()
}
