package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.csv.datasource.WarehouseDataSource as LocalWarehouseDataSource
import org.byte_bloom.flux.data.csv.mapper.toDomain
import org.byte_bloom.flux.data.remote.datasource.WarehouseDataSource as RemoteWarehouseDataSource
import org.byte_bloom.flux.data.remote.dto.toDomain
import org.byte_bloom.flux.data.remote.dto.toRequestDto
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class WarehouseRepositoryImpl(
    private val localDataSource: LocalWarehouseDataSource,
    private val remoteDataSource: RemoteWarehouseDataSource
) : WarehouseRepository {

    private var cachedWarehouses: List<Warehouse>? = null

    override suspend fun getAll(): List<Warehouse> = cachedWarehouses ?:
         localDataSource.getAll().map { it.toDomain() }
            .also { cachedWarehouses = it }


    override suspend fun getById(id: String): Result<Warehouse> = runCatching {
    remoteDataSource.getById(id)?.toDomain()
        ?: throw LogisticsException.EntityNotFoundException.WarehouseNotFoundException(id)}

    override suspend fun create(warehouse: Warehouse): Result<Warehouse> = runCatching {
        remoteDataSource.create(warehouse.toRequestDto()).toDomain()}

    override suspend fun update(id: String, warehouse: Warehouse): Result<Warehouse> = runCatching {
        remoteDataSource.update(id, warehouse.toRequestDto()).toDomain()}

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        remoteDataSource.delete(id)}
}
