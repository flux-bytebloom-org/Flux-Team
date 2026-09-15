package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.datasource.WarehouseDataSource
import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseWarehouses
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository


class WarehouseRepositoryImpl(
    private val warehouseDataSource: WarehouseDataSource
) : WarehouseRepository {

    private val warehouses: List<Warehouse> by lazy {
        warehouseDataSource.getAll().map { it.toDomain() }
    }

    override fun getAll(): List<Warehouse> = warehouses
}

