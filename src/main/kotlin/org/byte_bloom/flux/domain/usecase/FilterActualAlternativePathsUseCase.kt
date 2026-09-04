package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.WeightedPath

class FilterActualAlternativePathsUseCase {

    operator fun invoke(
        allPaths: List<WeightedPath>,
        bottleneckWarehouse: Warehouse,
        alternativePaths: Map<Pair<String, String>, List<Warehouse>>
    ): List<Pair<WeightedPath, List<Warehouse>>> {

        return allPaths
            .filter { weightedPath ->
                weightedPath.path
                    .drop(1)
                    .dropLast(1)
                    .any { it.id == bottleneckWarehouse.id }
            }
            .mapNotNull { weightedPath ->

                val origin = weightedPath.path.first()
                val destination = weightedPath.path.last()

                val alternativePath = alternativePaths[origin.id to destination.id]

                when {
                    alternativePath.isNullOrEmpty() -> null
                    alternativePath.map { it.id } == weightedPath.path.map { it.id } -> null
                    else -> weightedPath to alternativePath
                }
            }
    }
}
