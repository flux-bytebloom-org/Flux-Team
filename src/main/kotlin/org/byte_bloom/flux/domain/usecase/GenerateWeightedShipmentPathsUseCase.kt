package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.UseCaseException
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.WeightedPath

class GenerateWeightedShipmentPathsUseCase (private val findOptimalPathUseCase: FindOptimalPathUseCase) {
    operator fun invoke(
        uniqueRoutes: Map<Pair<String, String>, Int>,
        warehousesById: Map<String, Warehouse>
    ): List<WeightedPath> {
        return uniqueRoutes.mapNotNull { (routeKey, packageCount) ->
            val origin = warehousesById[routeKey.first]
                ?: throw UseCaseException.WarehouseNotFound(routeKey.first)
            val destination = warehousesById[routeKey.second]
                ?: throw UseCaseException.WarehouseNotFound(routeKey.second)

            val path = findOptimalPathUseCase(origin, destination)
            if (path.isEmpty()) null else WeightedPath(path, packageCount)
        }
    }
}

