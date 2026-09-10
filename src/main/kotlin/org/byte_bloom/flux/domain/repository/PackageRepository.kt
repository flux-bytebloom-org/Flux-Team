package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse

interface PackageRepository {
    fun getAll(): List<Package>
    fun updatePackageOriginHub(pkg: Package, hub: Warehouse): Package
}

