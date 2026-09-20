package org.byte_bloom.flux.data.remote.datasource

import org.byte_bloom.flux.data.remote.dto.RouteRequestDto
import org.byte_bloom.flux.data.remote.dto.RouteResponseDto


interface RemoteRouteDataSource {
    suspend fun getAll(): List<RouteResponseDto>
    suspend fun getById(id: String): RouteResponseDto?
    suspend fun create(dto: RouteRequestDto): RouteResponseDto
    suspend fun update(id: String, dto: RouteRequestDto): RouteResponseDto
    suspend fun delete(id: String)
}
