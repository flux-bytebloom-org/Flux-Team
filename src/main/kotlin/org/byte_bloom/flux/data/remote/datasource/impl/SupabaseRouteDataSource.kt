package org.byte_bloom.flux.data.remote.datasource.impl

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.byte_bloom.flux.data.remote.client.SupabaseHttpClient
import org.byte_bloom.flux.data.remote.client.requireSuccess
import org.byte_bloom.flux.data.remote.datasource.RemoteRouteDataSource
import org.byte_bloom.flux.data.remote.dto.RouteRequestDto
import org.byte_bloom.flux.data.remote.dto.RouteResponseDto
import org.byte_bloom.flux.data.remote.middleware.ResiliencePipeline

class SupabaseRouteDataSource (
    private val client : HttpClient = SupabaseHttpClient.client,
    private val pipeline: ResiliencePipeline
) : RemoteRouteDataSource {
    private val table = "routes"

    override suspend fun getAll(): List<RouteResponseDto> =
        pipeline.execute {
            withContext(Dispatchers.IO) {
                client.get(table).requireSuccess().body()
            }
        }

    override suspend fun getById(id: String): RouteResponseDto? =
        pipeline.execute {
            withContext(Dispatchers.IO) {
                client.get(table) {
                    parameter("id", "eq.$id")
                }.requireSuccess().body<List<RouteResponseDto>>().firstOrNull()
            }
        }

    override suspend fun create(dto: RouteRequestDto): RouteResponseDto =
        pipeline.execute {
            withContext(Dispatchers.IO) {
                client.post(table) {
                    header(HttpHeaders.Prefer, "return=representation")
                    setBody(dto)
                }.requireSuccess().body<List<RouteResponseDto>>().first()
            }
        }

    override suspend fun update(id: String, dto: RouteRequestDto): RouteResponseDto =
        pipeline.execute {
            withContext(Dispatchers.IO) {
                client.patch(table) {
                    parameter("id", "eq.$id")
                    header(HttpHeaders.Prefer, "return=representation")
                    setBody(dto)
                }.requireSuccess().body<List<RouteResponseDto>>().first()
            }
        }

    override suspend fun delete(id: String): RouteResponseDto? =
        pipeline.execute {
            withContext(Dispatchers.IO) {
                client.delete(table) {
                    parameter("id", "eq.$id")
                    header(HttpHeaders.Prefer, "return=representation")
                }.requireSuccess().body<List<RouteResponseDto>>().firstOrNull()
            }
        }

}
