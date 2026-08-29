package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse

class AssignPackageToCargoQueueUseCase {

    operator fun invoke(hub: Warehouse, pkg: Package) {
        hub.addPackage(pkg)
        hub.sortCargoQueue()
    }
}

