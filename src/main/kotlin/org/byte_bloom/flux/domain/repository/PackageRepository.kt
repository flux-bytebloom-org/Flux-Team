package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse

interface PackageRepository {
    fun getAll(): List<Package>

    /** Updates the package's origin hub AND places it in that hub's cargo queue — two steps, one call. */
    fun updatePackageOriginHub(pkg: Package, hub: Warehouse): Package
    fun removePackageFromHub(pkg: Package, hub: Warehouse)
    fun updatePackageDestination(pkg: Package, newDestination: Warehouse) : Package
}

