package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.remote.datasource.RemoteWarehouseDataSource
import org.byte_bloom.flux.data.remote.mapper.WarehouseDtoMapper
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class SupabaseWarehouseRepositoryImpl(
    private val remoteDataSource: RemoteWarehouseDataSource,
    private val dtoMapper: WarehouseDtoMapper
) : WarehouseRepository {

    override suspend fun getAll(): List<Warehouse> =
        remoteDataSource.getAll().map { dto -> dtoMapper.toDomain(dto) }
}