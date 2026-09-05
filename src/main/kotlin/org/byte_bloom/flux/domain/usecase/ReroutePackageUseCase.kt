package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.UseCaseException
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.ui.utils.logWarning

class ReroutePackageUseCase {
    operator fun invoke(
        originHub: Warehouse,
        packageItem: Package,
        newDestination: Warehouse
    ): Package {
        val actualPackage = originHub.getCargoQueue().find { it.id == packageItem.id }
            ?: throw UseCaseException.PackageNotInQueue(
                packageId = packageItem.id,
                warehouseId = originHub.id
            )
        val reroutedPackage = actualPackage.copy(destinationHub = newDestination)
        originHub.removePackage(actualPackage)
        originHub.addPackage(reroutedPackage)

        return reroutedPackage
    }
}
