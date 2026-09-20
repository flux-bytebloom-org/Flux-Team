package org.byte_bloom.flux.data.remote.datasource

import org.byte_bloom.flux.data.remote.dto.PackageRequestDto
import org.byte_bloom.flux.data.remote.dto.PackageResponseDto

interface RemotePackageDataSource {
    suspend fun getAll(): List<PackageResponseDto>
    suspend fun getById(id: String): PackageResponseDto?
    suspend fun create(dto: PackageRequestDto): PackageResponseDto
    suspend fun update(id: String, dto: PackageRequestDto): PackageResponseDto
    suspend fun delete(id: String)
}
