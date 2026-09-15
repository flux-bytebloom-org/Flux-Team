package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.datasource.PackageDataSource
import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parsePackages
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class PackageRepositoryImpl(
    private val pkgDataSource: PackageDataSource,
    private val warehouseRepository: WarehouseRepository

) : PackageRepository {

    override fun getAll(): List<Package> {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return pkgDataSource.getAll()
            .mapNotNull { it.toDomain(warehousesById) }
            .onEach { pkg -> pkg.originHub.addPackage(pkg) }
    }

    override fun updatePackageOriginHub(pkg: Package, hub: Warehouse): Package {
        pkgDataSource.updateOriginHub(pkg.id,hub.id)
        return pkg.copy(originHub = hub)
    }

    override fun removePackageFromHub(pkg: Package, hub: Warehouse) {
        pkgDataSource.removeFromHub(pkg.id,hub.id)
    }

    override fun updatePackageDestination(pkg: Package, newDestination: Warehouse): Package {
        pkgDataSource.updateDestination(pkg.id,newDestination.id)
        return pkg.copy(destinationHub = newDestination)
    }
}
