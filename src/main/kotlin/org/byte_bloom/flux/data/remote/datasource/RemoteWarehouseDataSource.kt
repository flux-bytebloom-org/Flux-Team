package org.byte_bloom.flux.data.remote.datasource

import io.ktor.client.call.body
import io.ktor.client.request.get
import org.byte_bloom.flux.data.remote.client.SupabaseHttpClient
import org.byte_bloom.flux.data.remote.dto.warehouse.WarehouseResponseDto

class RemoteWarehouseDataSource {
    private val client = SupabaseHttpClient.client
    private val table = "warehouses"

    suspend fun getAll(): List<WarehouseResponseDto> =
        client.get(table).body()
}
