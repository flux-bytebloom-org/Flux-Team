package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse

interface WarehouseRepository {
    fun getAll(): List<Warehouse>
    fun removePackageFromCargoQueue(hub: Warehouse, pkg: Package)
}

