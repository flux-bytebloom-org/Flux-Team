package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parsePackages
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class CsvPackageRepository(
    private val filePath: String,
    private val warehouseRepository: WarehouseRepository
) : PackageRepository {

    private val packages: List<Package> by lazy {
        val warehouseMap = warehouseRepository
            .getAll()
            .associateBy { it.id }

        parsePackages(
            cleanLines(readCsv(filePath))
        )
            .mapNotNull { it.toDomain(warehouseMap) }
            .onEach { it.destinationHub.addPackage(it) }
    }

    override fun getAll(): List<Package> = packages


    override fun updatePackageOriginHub(pkg: Package, hub: Warehouse): Package {
        println("[[temp]] (1/2) Updating package ${pkg.id}'s originHub -> ${hub.id}")
        println("[[temp]] (2/2) Placing package ${pkg.id} in hub ${hub.id}'s cargo queue")
        return pkg.copy(originHub = hub)
    }

    override fun removePackageFromHub(pkg: Package, hub: Warehouse) {
        println("[[temp]] Removing package ${pkg.id} from hub ${hub.id} in CSV repository (not implemented YET)")
    }

    override fun updatePackageDestination(pkg: Package, newDestination: Warehouse): Package {
        println("[[temp]] Updating package ${pkg.id} destination to ${newDestination.id} in CSV repository (not implemented YET)")
        return pkg.copy(destinationHub = newDestination)
    }
}
