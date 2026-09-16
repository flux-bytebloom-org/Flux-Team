package org.byte_bloom.flux.data.csv.datasource

import org.byte_bloom.flux.data.csv.dataholders.PackageRaw

interface PackageDataSource {
    fun getAll(): List<PackageRaw>

    /** Updates the package's origin hub AND places it in that hub's cargo sorted queue — two steps, one call. */
    fun updateOriginHub(packageId: String, hubId: String)

    /** Removes the package's hub association AND removes it from that hub's cargo queue — two steps, one call. */
    fun removeFromHub(packageId: String, hubId: String)

    fun updateDestination(packageId: String, hubId: String)
}
