package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Warehouse

interface WarehouseRepository {
    suspend fun getAll(): List<Warehouse>
    suspend fun getById(id: String): Warehouse?
    suspend fun create(warehouse: Warehouse): Warehouse
    suspend fun update(id: String, warehouse: Warehouse): Warehouse
    suspend fun delete(id: String)
}