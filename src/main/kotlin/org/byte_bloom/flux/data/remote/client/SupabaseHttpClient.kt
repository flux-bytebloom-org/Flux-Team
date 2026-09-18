package org.byte_bloom.flux.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.byte_bloom.flux.data.remote.config.SupabaseConfig

object SupabaseHttpClient {
    val json = Json { ignoreUnknownKeys = true; isLenient = true }
    val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(json) }
        defaultRequest {
            url(SupabaseConfig.BASE_URL)
            header("apikey", SupabaseConfig.API_KEY)
            header(HttpHeaders.Authorization, "Bearer ${SupabaseConfig.API_KEY}")
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }
}
