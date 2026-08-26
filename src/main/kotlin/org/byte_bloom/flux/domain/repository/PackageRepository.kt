package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Package

interface PackageRepository {
    fun getAll(): List<Package>
}
