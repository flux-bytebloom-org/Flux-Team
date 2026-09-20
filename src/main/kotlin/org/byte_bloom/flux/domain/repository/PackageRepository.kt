package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse

interface PackageRepository {
    suspend fun getAll(): List<Package>

    suspend fun getById(id: String): Package

    suspend fun create(pkg: Package): Package

    suspend fun update(id: String, pkg: Package): Package

    suspend fun delete(id: String)

    /** Updates the package's origin hub AND places it in that hub's cargo sorted queue. */
    fun updatePackageOriginHub(pkg: Package, hub: Warehouse): Package

    /** Removes the package's hub association AND removes it from that hub's cargo queue*/
    fun removePackageFromHub(pkg: Package, hub: Warehouse)

    fun updatePackageDestination(pkg: Package, newDestination: Warehouse): Package
}