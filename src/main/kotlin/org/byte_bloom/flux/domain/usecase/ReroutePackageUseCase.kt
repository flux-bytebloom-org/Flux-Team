package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.PackageNotInQueueException
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse

class ReroutePackageUseCase {
    operator fun invoke(
        originHub: Warehouse,
        packageItem: Package,
        newDestination: Warehouse
    ): Package {

        if (packageItem !in originHub.getCargoQueue()) {
            throw PackageNotInQueueException(
                "Package ${packageItem.id} is not queued at hub ${originHub.id}"
            )
        }

        val reroutedPackage = packageItem.copy(destinationHub = newDestination)

        originHub.removePackage(packageItem)
        originHub.addPackage(reroutedPackage)

        return reroutedPackage
    }
}
