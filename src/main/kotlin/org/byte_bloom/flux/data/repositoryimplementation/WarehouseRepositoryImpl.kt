package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.csv.datasource.WarehouseDataSource
import org.byte_bloom.flux.data.csv.mapper.toDomain
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository


class WarehouseRepositoryImpl(
    private val warehouseDataSource: WarehouseDataSource
) : WarehouseRepository {

    private val warehouses: List<Warehouse> by lazy {
        warehouseDataSource.getAll().map { it.toDomain() }
    }

    override suspend fun getAll(): List<Warehouse> = warehouses
}

