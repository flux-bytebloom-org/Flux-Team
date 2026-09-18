package org.byte_bloom.flux.data.remote.datasource

import org.byte_bloom.flux.data.remote.dto.WarehouseRequestDto
import org.byte_bloom.flux.data.remote.dto.WarehouseResponseDto

interface WarehouseDataSource {
    suspend fun getAll(): List<WarehouseResponseDto>
    suspend fun getById(id: String): WarehouseResponseDto?
    suspend fun create(dto: WarehouseRequestDto): WarehouseResponseDto
    suspend fun update(id: String, dto: WarehouseRequestDto): WarehouseResponseDto
    suspend fun delete(id: String)
}