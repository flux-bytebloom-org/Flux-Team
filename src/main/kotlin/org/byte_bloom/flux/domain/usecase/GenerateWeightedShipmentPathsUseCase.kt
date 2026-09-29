package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.WeightedPath

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
                    ?: throw LogisticsException.EntityNotFoundException.WarehouseNotFoundException(routeKey.first)
                val destination = warehousesById[routeKey.second.uppercase()]
                    ?: throw LogisticsException.EntityNotFoundException.WarehouseNotFoundException(routeKey.second)

                val path = findOptimalPathUseCase(origin, destination)
                if (path.isEmpty()) null else WeightedPath(path, packageCount)

            } catch (e: LogisticsException.EntityNotFoundException.WarehouseNotFoundException) {
                skippedCount++
                println("WARNING: Skipping route $routeKey: ${e.message}")
                null
            }
        }

        println("Total routes: ${uniqueRoutes.size}, skipped: $skippedCount, valid: ${result.size}")
        return result
    }
}
