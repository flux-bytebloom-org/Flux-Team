package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.SplitRerouteResult
import org.byte_bloom.flux.domain.response.WeightedPath

class SplitAndRerouteShipmentsUseCase(
    private val reroutePackageUseCase: ReroutePackageUseCase
) {

    operator fun invoke(
        originalPath: WeightedPath,
        newPath: List<Warehouse>,
        splitRatio: Double,
        allPackages: List<Package>,
        currentRoutePlans: Map<String, List<Warehouse>>
    ): SplitRerouteResult {

        val origin = originalPath.path.first()
        val destination = originalPath.path.last()

        val matchedPackages = allPackages.filter {
            it.originHub.id == origin.id && it.destinationHub.id == destination.id
        }

        val packagesToMoveCount = (matchedPackages.size * splitRatio).toInt()

        if (packagesToMoveCount == 0) {
            return SplitRerouteResult(
                reroutedPackages = allPackages,
                updatedRoutePlans = currentRoutePlans
            )
        }

        val (toReroute, toKeep) = matchedPackages
            .sortedBy { it.priority }
            .let { sorted -> sorted.take(packagesToMoveCount) to sorted.drop(packagesToMoveCount) }

        // ملاحظة: الوجهة النهائية (destination) هي نفسها — إحنا بس بنبدّل
        // "خطة المسار" اللي الطرد ماشي عليها، مش وجهته الحقيقية.
        val reroutedSubset = toReroute.map { packageItem ->
            reroutePackageUseCase(origin, packageItem, destination)
        }

        val unaffectedPackages = allPackages.filterNot { packageItem ->
            matchedPackages.any { it.id == packageItem.id }
        }

        val routePlanUpdates = toReroute.associate { it.id to newPath }

        return SplitRerouteResult(
            reroutedPackages = unaffectedPackages + reroutedSubset + toKeep,
            updatedRoutePlans = currentRoutePlans + routePlanUpdates
        )
    }
}
