package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parsePackages
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Package
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
}
