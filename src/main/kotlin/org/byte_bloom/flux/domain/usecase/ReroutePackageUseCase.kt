package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.UseCaseException
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse

class ReroutePackageUseCase {
    operator fun invoke(
        originHub: Warehouse,
        packageItem: Package,
        newDestination: Warehouse
    ): Package {

        if (packageItem !in originHub.getCargoQueue()) {
            throw UseCaseException.PackageNotInQueue(
                packageId = packageItem.id,
                warehouseId = originHub.id
            )
        }
        val reroutedPackage = packageItem.copy(destinationHub = newDestination)

        originHub.removePackage(packageItem)
        originHub.addPackage(reroutedPackage)

        return reroutedPackage
    }
}
