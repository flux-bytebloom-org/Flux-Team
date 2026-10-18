package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.mapper.toCargoQuery
import org.byte_bloom.flux.domain.mapper.toCargoRecords
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.RegionalZone

class ClassifyPackageByIdUseCase(
    private val classifyPackageSector: ClassifyPackageSectorUseCase
) {

    operator fun invoke(packageId: String, allPackages: List<Package>): RegionalZone {
        val target = allPackages.find { it.id == packageId }
            ?: throw LogisticsException.EntityNotFoundException.PackageNotFoundException(packageId)

        val query = target.toCargoQuery()
            ?: throw LogisticsException.ValidationException.InvalidPackageWeightException(
                "package ${target.id} has invalid weight: ${target.weight}"
            )

        val trainingRecords = allPackages.toCargoRecords(excludingId = target.id)

        return classifyPackageSector(query, trainingRecords)
    }
}
