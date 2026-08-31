package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.WeightedPath


class FindBottleneckWarehouseUseCase {
    operator fun invoke(weightedPaths: List<WeightedPath>): Warehouse? {
        return weightedPaths
            .flatMap { weightedPath -> weightedPath.path
                    .drop(1)
                    .dropLast(1)
                    .map { intermediateStop -> intermediateStop to weightedPath.packageCount }
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { entry -> entry.value.sum() }
            .maxByOrNull { it.value }
            ?.key
    }
}