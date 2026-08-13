package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.data.dataholders.WarehouseRaw

interface WarehouseRepository {
    fun getAll(): List<WarehouseRaw>
}