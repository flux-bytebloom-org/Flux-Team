package org.byte_bloom.flux.domain.repo

interface WarehouseRepo {
    fun addPackageToCargoQueue(warehouseId: String, packageId: String):Package
}

