package org.byte_bloom.flux.data.remote.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.byte_bloom.flux.data.remote.client.SupabaseHttpClient
import org.byte_bloom.flux.data.remote.dto.PackageRequestDto
import org.byte_bloom.flux.data.remote.dto.PackageResponseDto

class SupabasePackageDataSource(
    private val client: HttpClient = SupabaseHttpClient.client
) :RemotePackageDataSource {
    private val table = "packages"

    override suspend fun getAll(): List<PackageResponseDto> =
        client.get(table).body()

    override suspend fun getById(id: String): PackageResponseDto =
        client.get("$table?id=eq.$id").body()

    override suspend fun create(dto: PackageRequestDto): PackageResponseDto =
        client.post(table) {
            setBody(dto)
        }.body()

    override suspend fun update(id: String, dto: PackageRequestDto): PackageResponseDto =
        client.patch("$table?id=eq.$id") {
            setBody(dto)
        }.body()

    override suspend fun delete(id: String) {
        client.delete("$table?id=eq.$id")
    }
}
