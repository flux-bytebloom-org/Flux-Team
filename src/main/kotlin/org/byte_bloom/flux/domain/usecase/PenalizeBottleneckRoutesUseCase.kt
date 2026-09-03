package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Warehouse

class PenalizeBottleneckRoutesUseCase {

    operator fun invoke(
        bottleneckWarehouse: Warehouse,
        allWarehouses: List<Warehouse>,
        penaltyFactor: Double
    ): Map<String, Warehouse> {
        val shadowWarehousesById = allWarehouses.associate { warehouse ->
            warehouse.id to copyWarehouse(warehouse)
        }

        allWarehouses.forEach { warehouse ->
            warehouse.getOutgoingRoutes().forEach { route ->
                addAdjustedRoute(
                    route,
                    bottleneckWarehouse,
                    penaltyFactor,
                    shadowWarehousesById
                )
            }
        }

        return shadowWarehousesById
    }

    private fun copyWarehouse(warehouse: Warehouse): Warehouse {
        return Warehouse(
            id = warehouse.id,
            name = warehouse.name,
            regionalZone = warehouse.regionalZone,
            latitude = warehouse.latitude,
            longitude = warehouse.longitude
        )
    }

    private fun addAdjustedRoute(
        route: Route,
        bottleneckWarehouse: Warehouse,
        penaltyFactor: Double,
        shadowWarehousesById: Map<String, Warehouse>
    ) {
        val touchesBottleneck =
            route.originHub.id == bottleneckWarehouse.id ||
                    route.destinationHub.id == bottleneckWarehouse.id

        val adjustedDistance =
            if (touchesBottleneck) route.distanceKm * penaltyFactor else route.distanceKm

        val shadowOrigin = shadowWarehousesById.getValue(route.originHub.id)
        val shadowDestination = shadowWarehousesById.getValue(route.destinationHub.id)

        shadowOrigin.addRoute(
            Route(
                id = route.id,
                distanceKm = adjustedDistance,
                typicalDelayMin = route.typicalDelayMin,
                originHub = shadowOrigin,
                destinationHub = shadowDestination
            )
        )
    }
}
