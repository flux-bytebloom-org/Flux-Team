package org.byte_bloom.flux.data.remote.datasource

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import org.byte_bloom.flux.data.remote.client.SupabaseHttpClient
import org.byte_bloom.flux.data.remote.dto.RouteRequestDto
import org.byte_bloom.flux.data.remote.dto.RouteResponseDto

class RemoteRouteDataSource {
    private val client = SupabaseHttpClient.client
    private val table = "routes"

    suspend fun getAll(): List<RouteResponseDto> =
        client.get(table).body()

    suspend fun getById(id: String): RouteResponseDto? =
        client.get(table) {
            parameter("id", "eq.$id")
        }.body<List<RouteResponseDto>>().firstOrNull()

    suspend fun create(dto: RouteRequestDto): RouteResponseDto {
        return client.post(table) {
            header(HttpHeaders.Prefer, "return=representation")
            setBody(dto)
        }.body<List<RouteResponseDto>>().first()
    }

    suspend fun update(id: String, dto: RouteRequestDto): RouteResponseDto {
        return client.patch(table) {
            parameter("id", "eq.$id")
            header(HttpHeaders.Prefer, "return=representation")
            setBody(dto)
        }.body<List<RouteResponseDto>>().first()
    }

    suspend fun delete(id: String) {
        client.delete(table) {
            parameter("id", "eq.$id")
        }
    }
}
