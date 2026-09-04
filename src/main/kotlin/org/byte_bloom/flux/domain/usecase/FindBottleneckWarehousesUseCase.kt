package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.UseCaseException
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.WeightedPath
import org.byte_bloom.flux.domain.response.BottleneckWarehouse

class FindBottleneckWarehousesUseCase {
    operator fun invoke(
        weightedPaths: List<WeightedPath>,
        minTransitLoad: Int
    ): List<BottleneckWarehouse> {

        if (minTransitLoad < 0) {
            throw UseCaseException.InvalidTransitLoad(
                "Minimum transit load cannot be negative"
            )
        }

        return weightedPaths
            .flatMap { weightedPath ->
                weightedPath.path
                    .drop(1)
                    .dropLast(1)
                    .map { intermediateStop -> intermediateStop to weightedPath.packageCount }
            }
            .groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            )
            .mapValues { entry -> entry.value.sum() }
            .filter { it.value >= minTransitLoad }
            .map { (warehouse, transitLoad) -> BottleneckWarehouse(warehouse, transitLoad) }
            .sortedByDescending { it.transitLoad }
    }
}



