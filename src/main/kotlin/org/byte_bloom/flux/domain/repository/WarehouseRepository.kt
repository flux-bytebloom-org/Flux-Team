package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Warehouse

interface WarehouseRepository {
    fun getAll(): List<Warehouse>
}

