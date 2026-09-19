package org.byte_bloom.flux.data.remote.datasource.impl

import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.byte_bloom.flux.data.remote.client.SupabaseHttpClient
import org.byte_bloom.flux.data.remote.datasource.WarehouseDataSource
import org.byte_bloom.flux.data.remote.dto.WarehouseRequestDto
import org.byte_bloom.flux.data.remote.dto.WarehouseResponseDto

class SupabaseWarehouseDataSource( private val client: HttpClient = SupabaseHttpClient.client
) : WarehouseDataSource {
    private val table = "warehouses"

    override suspend fun getAll(): List<WarehouseResponseDto> =
        client.get(table).body()

    override suspend fun getById(id: String): WarehouseResponseDto? =
        client.get(table) { url { parameters.append("id", "eq.$id") } }
            .body<List<WarehouseResponseDto>>().firstOrNull()

    override suspend fun create(dto: WarehouseRequestDto): WarehouseResponseDto =
        client.post(table) {
            header("Prefer", "return=representation")
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body<List<WarehouseResponseDto>>().first()

    override suspend fun update(id: String, dto: WarehouseRequestDto): WarehouseResponseDto =
        client.patch(table) {
            url { parameters.append("id", "eq.$id") }
            header("Prefer", "return=representation")
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body<List<WarehouseResponseDto>>().first()

    override suspend fun delete(id: String) {
        client.delete(table) { url { parameters.append("id", "eq.$id") } }
    }
}
