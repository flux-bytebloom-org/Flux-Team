package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Warehouse

interface WarehouseRepository {
    suspend fun getAll(): List<Warehouse>
    suspend fun getById(id: String): Result<Warehouse>
    suspend fun create(warehouse: Warehouse): Result<Warehouse>
    suspend fun update(id: String, warehouse: Warehouse): Result<Warehouse>
    suspend fun delete(id: String): Result<Unit>
}