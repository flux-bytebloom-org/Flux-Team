package org.byte_bloom.flux.domain.repository
import org.byte_bloom.flux.data.dataholders.PackageRaw
interface PackageRepository {
    fun getAll(): List<PackageRaw>
}
