package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class AssignPackageToCargoQueueUseCase(
    val warehouseRepo: WarehouseRepository,
    val packageRepo: PackageRepository
) {

    operator fun invoke(hub: Warehouse, pkg: Package) {
        packageRepo.updatePackageOriginHub(pkg, hub)
        warehouseRepo.updateWarehouseCargoQueue(hub, pkg)
        /* old code
        hub.addPackage(pkg)
        hub.sortCargoQueue() // to do

         */
    }
}

