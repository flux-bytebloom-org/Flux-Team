package org.byte_bloom.flux.data.remote.datasource.impl

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.byte_bloom.flux.data.remote.client.SupabaseHttpClient
import org.byte_bloom.flux.data.remote.datasource.RemoteVehicleDataSource
import org.byte_bloom.flux.data.remote.dto.VehicleRequestDto
import org.byte_bloom.flux.data.remote.dto.VehicleResponseDto

class SupabaseVehicleDataSource(
    private val client: HttpClient = SupabaseHttpClient.client
) : RemoteVehicleDataSource {

    private val table = "vehicles"

    override suspend fun getAll(): List<VehicleResponseDto> =
        client.get(table).body()

    override suspend fun getById(id: String): VehicleResponseDto? =
        client.get(table) { url { parameters.append("id", "eq.$id") } }
            .body<List<VehicleResponseDto>>().firstOrNull()

    override suspend fun create(dto: VehicleRequestDto): VehicleResponseDto =
        client.post(table) {
            header("Prefer", "return=representation")
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body<List<VehicleResponseDto>>().first()

    override suspend fun update(id: String, dto: VehicleRequestDto): VehicleResponseDto =
        client.patch(table) {
            url { parameters.append("id", "eq.$id") }
            header("Prefer", "return=representation")
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body<List<VehicleResponseDto>>().first()

    override suspend fun delete(id: String) {
        client.delete(table) { url { parameters.append("id", "eq.$id") } }
    }
}
