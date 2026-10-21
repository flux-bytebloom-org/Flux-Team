
package org.byte_bloom.flux.domain.exception

import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

fun isTransient(e: Throwable): Boolean { var current: Throwable? = e

    while (current != null) {
        when (current) {
            is CancellationException,
            is LogisticsException.ValidationException,
            is LogisticsException.DataAccessException.CallNotPermittedException -> return false
        }

        if (current is LogisticsException.DataAccessException.RemoteHttpException) {
            return current.statusCode == 408 ||
                    current.statusCode == 429 ||
                    current.statusCode in 500..599
        }

        current = current.cause
    }

    current = e
    while (current != null) {
        when (current) {
            is LogisticsException.DataAccessException.NetworkUnavailableException,
            is IOException -> return true
        }

        current = current.cause
    }

    return false
}
