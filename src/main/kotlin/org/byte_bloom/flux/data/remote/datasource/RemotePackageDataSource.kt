package org.byte_bloom.flux.data.remote.datasource

import io.ktor.client.call.*
import io.ktor.client.request.*
import org.byte_bloom.flux.data.remote.client.SupabaseHttpClient
import org.byte_bloom.flux.data.remote.dto.PackageRequestDto
import org.byte_bloom.flux.data.remote.dto.PackageResponseDto

class RemotePackageDataSource {
    private val client = SupabaseHttpClient.client
    private val table = "packages"

    suspend fun getAll(): List<PackageResponseDto> =
        client.get(table).body()

    suspend fun getById(id: String): PackageResponseDto =
        client.get("$table?id=eq.$id").body()

    suspend fun create(request: PackageRequestDto): PackageResponseDto =
        client.post(table) {
            setBody(request)
        }.body()

    suspend fun update(id: String, request: PackageRequestDto): PackageResponseDto =
        client.patch("$table?id=eq.$id") {
            setBody(request)
        }.body()

    suspend fun delete(id: String) {
        client.delete("$table?id=eq.$id")
    }
}
