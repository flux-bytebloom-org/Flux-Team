package org.byte_bloom.flux.data.repositoryimplementation

import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.data.remote.datasource.RemotePackageDataSource
import org.byte_bloom.flux.data.remote.dto.PackageRequestDto
import org.byte_bloom.flux.data.remote.dto.toDomain
import org.byte_bloom.flux.data.remote.dto.toRequestDto
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class SupabasePackageRepositoryImpl(
    private val remoteDataSource: RemotePackageDataSource,
    private val warehouseRepository: WarehouseRepository
) : PackageRepository {

    override suspend fun getAll(): List<Package> {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return remoteDataSource.getAll()
            .mapNotNull { it.toDomain(warehousesById) }
            .onEach { pkg -> pkg.originHub.addPackage(pkg) }
    }

    override suspend fun getById(id: String): Package {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return requireNotNull(remoteDataSource.getById(id).toDomain(warehousesById)) {
            "Package $id references an unknown warehouse"
        }
    }

    override suspend fun create(pkg: Package): Package {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        val response = remoteDataSource.create(pkg.toRequestDto())
        return requireNotNull(response.toDomain(warehousesById)) {
            "Created package references an unknown warehouse"
        }
    }

    override suspend fun update(id: String, pkg: Package): Package {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        val response = remoteDataSource.update(id, pkg.toRequestDto())
        return requireNotNull(response.toDomain(warehousesById)) {
            "Updated package references an unknown warehouse"
        }
    }

    override suspend fun delete(id: String) {
        remoteDataSource.delete(id)
    }

    override fun updatePackageOriginHub(pkg: Package, hub: Warehouse): Package {
        val requestDto = pkg.toUpdatedRequestDto(originHub = hub)
        runBlocking { remoteDataSource.update(pkg.id, requestDto) }
        return pkg.copy(originHub = hub)
    }

    override fun removePackageFromHub(pkg: Package, hub: Warehouse) {
        runBlocking { remoteDataSource.delete(pkg.id) }
    }

    override fun updatePackageDestination(pkg: Package, newDestination: Warehouse): Package {
        val requestDto = pkg.toUpdatedRequestDto(destinationHub = newDestination)
        runBlocking { remoteDataSource.update(pkg.id, requestDto) }
        return pkg.copy(destinationHub = newDestination)
    }

    private fun Package.toUpdatedRequestDto(
        originHub: Warehouse = this.originHub,
        destinationHub: Warehouse = this.destinationHub
    ): PackageRequestDto {
        return PackageRequestDto(
            weight = weight,
            originHubId = originHub.id,
            destinationHubId = destinationHub.id,
            priority = priority.name
        )
    }
}