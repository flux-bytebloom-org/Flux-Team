package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.WeightedPath


class FindBottleneckWarehousesUseCase {
    operator fun invoke(weightedPaths: List<WeightedPath>, minTransitLoad: Int): List<Warehouse> {
        return weightedPaths
            .flatMap { weightedPath ->
                weightedPath.path
                    .drop(1)
                    .dropLast(1)
                    .map { intermediateStop -> intermediateStop to weightedPath.packageCount }
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { entry -> entry.value.sum() }
            .filter { it.value >= minTransitLoad }
            .toList()
            .sortedByDescending { it.second }
            .map { it.first }
    }
}
