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
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.byte_bloom.flux.data.remote.client.SupabaseHttpClient
import org.byte_bloom.flux.data.remote.client.requireSuccess
import org.byte_bloom.flux.data.remote.datasource.RemotePackageDataSource
import org.byte_bloom.flux.data.remote.dto.PackageRequestDto
import org.byte_bloom.flux.data.remote.dto.PackageResponseDto


class SupabasePackageDataSource(
    private val client: HttpClient = SupabaseHttpClient.client
) : RemotePackageDataSource {
    private val table = "packages"

    override suspend fun getAll(): List<PackageResponseDto>  =
        withContext(Dispatchers.IO) { client.get(table).requireSuccess().body() }

    override suspend fun getById(id: String): PackageResponseDto? = withContext(Dispatchers.IO) {
        client.get(table) {
            parameter("id", "eq.$id")
        }.requireSuccess().body<List<PackageResponseDto>>().firstOrNull()
    }

    override suspend fun create(dto: PackageRequestDto): PackageResponseDto = withContext(Dispatchers.IO) {
        client.post(table) {
            header("Prefer", "return=representation")
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.requireSuccess().body<List<PackageResponseDto>>().first()
    }

    override suspend fun update(id: String, dto: PackageRequestDto): PackageResponseDto = withContext(Dispatchers.IO) {
        client.patch(table) {
            parameter("id", "eq.$id")
            header("Prefer", "return=representation")
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.requireSuccess().body<List<PackageResponseDto>>().first()
    }

    override suspend fun delete(id: String): PackageResponseDto? = withContext(Dispatchers.IO) {
        client.delete(table) {
            url { parameters.append("id", "eq.$id") }
            header("Prefer", "return=representation")
        }.requireSuccess().body<List<PackageResponseDto>>().firstOrNull()
    }
}

