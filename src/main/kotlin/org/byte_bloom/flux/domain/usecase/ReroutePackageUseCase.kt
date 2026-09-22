package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository


class ReroutePackageUseCase (
    val packageRepo: PackageRepository
) {
    operator fun invoke(
        originHub: Warehouse,
        packageItem: Package,
        newDestination: Warehouse,
    ): Package {
        val actualPackage = originHub.getCargoQueue().find { it.id == packageItem.id }
            ?: throw LogisticsException.BusinessLogicException.PackageNotInQueueException(
                packageId = packageItem.id,
                warehouseId = originHub.id
            )
        val reroutedPackage = packageRepo.updatePackageDestination(actualPackage, newDestination)

        return reroutedPackage
    }
}
