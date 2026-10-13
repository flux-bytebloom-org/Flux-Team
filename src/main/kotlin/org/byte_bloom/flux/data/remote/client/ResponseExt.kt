package org.byte_bloom.flux.data.remote.client

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import org.byte_bloom.flux.domain.exception.LogisticsException

internal suspend fun HttpResponse.requireSuccess(): HttpResponse {
    if (!status.isSuccess()) {
        throw LogisticsException.DataAccessException.DatabaseConflictException(
            "HTTP ${status.value}: ${bodyAsText()}"
        )
    }
    return this
}
