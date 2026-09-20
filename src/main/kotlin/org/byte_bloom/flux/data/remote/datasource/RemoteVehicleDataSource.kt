package org.byte_bloom.flux.data.remote.datasource

import org.byte_bloom.flux.data.remote.dto.VehicleRequestDto
import org.byte_bloom.flux.data.remote.dto.VehicleResponseDto

interface RemoteVehicleDataSource {
    suspend fun getAll(): List<VehicleResponseDto>
    suspend fun getById(id: String): VehicleResponseDto?
    suspend fun create(dto: VehicleRequestDto): VehicleResponseDto
    suspend fun update(id: String, dto: VehicleRequestDto): VehicleResponseDto
    suspend fun delete(id: String)
}

