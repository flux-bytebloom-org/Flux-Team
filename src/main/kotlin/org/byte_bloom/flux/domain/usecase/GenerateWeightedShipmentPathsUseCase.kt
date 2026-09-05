package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.UseCaseException
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.WeightedPath
import org.byte_bloom.flux.ui.utils.logWarning

class GenerateWeightedShipmentPathsUseCase(
    private val findOptimalPathUseCase: FindOptimalPathUseCase
) {
    operator fun invoke(
        uniqueRoutes: Map<Pair<String, String>, Int>,
        warehousesById: Map<String, Warehouse>
    ): List<WeightedPath> {
        var skippedCount = 0

        val result = uniqueRoutes.mapNotNull { (routeKey, packageCount) ->
            try {
                val origin = warehousesById[routeKey.first.uppercase()]
                    ?: throw UseCaseException.WarehouseNotFound(routeKey.first)
                val destination = warehousesById[routeKey.second.uppercase()]
                    ?: throw UseCaseException.WarehouseNotFound(routeKey.second)

                val path = findOptimalPathUseCase(origin, destination)
                if (path.isEmpty()) null else WeightedPath(path, packageCount)

            } catch (e: UseCaseException.WarehouseNotFound) {
                skippedCount++
                logWarning("Skipping route $routeKey: ${e.message}")
                null
            }
        }

        println("Total routes: ${uniqueRoutes.size}, skipped: $skippedCount, valid: ${result.size}")
        return result
    }
}
