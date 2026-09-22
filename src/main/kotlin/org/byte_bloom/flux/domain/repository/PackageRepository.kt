package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse

interface PackageRepository {
    suspend fun getAll(): List<Package>

    suspend fun getById(id: String): Result<Package>

    suspend fun create(pkg: Package): Result<Package>

    suspend fun update(id: String, pkg: Package): Result<Package>

    suspend fun delete(id: String): Result<Unit>

    fun updatePackageOriginHub(pkg: Package, hub: Warehouse): Package

    fun removePackageFromHub(pkg: Package, hub: Warehouse)

    fun updatePackageDestination(pkg: Package, newDestination: Warehouse): Package
}