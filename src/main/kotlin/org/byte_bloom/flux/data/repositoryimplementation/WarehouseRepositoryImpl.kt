package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.csv.datasource.WarehouseDataSource as LocalWarehouseDataSource
import org.byte_bloom.flux.data.csv.mapper.toDomain
import org.byte_bloom.flux.data.remote.datasource.WarehouseDataSource as RemoteWarehouseDataSource
import org.byte_bloom.flux.data.remote.dto.toDomain
import org.byte_bloom.flux.data.remote.dto.toRequestDto
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class WarehouseRepositoryImpl(
    private val localDataSource: LocalWarehouseDataSource,
    private val remoteDataSource: RemoteWarehouseDataSource
) : WarehouseRepository {

    override suspend fun getAll(): List<Warehouse> =
        localDataSource.getAll().map { it.toDomain() }

    override suspend fun getById(id: String): Warehouse? =
        remoteDataSource.getById(id)?.toDomain()

    override suspend fun create(warehouse: Warehouse): Warehouse =
        remoteDataSource.create(warehouse.toRequestDto()).toDomain()

    override suspend fun update(id: String, warehouse: Warehouse): Warehouse =
        remoteDataSource.update(id, warehouse.toRequestDto()).toDomain()

    override suspend fun delete(id: String) =
        remoteDataSource.delete(id)
}
