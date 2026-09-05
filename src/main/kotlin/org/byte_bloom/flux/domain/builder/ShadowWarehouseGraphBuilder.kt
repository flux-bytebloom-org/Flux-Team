package org.byte_bloom.flux.domain.builder

import org.byte_bloom.flux.domain.exception.UseCaseException
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Warehouse

class ShadowWarehouseGraphBuilder {

    fun build(
        allWarehouses: List<Warehouse>,
        bottleneckWarehouse: Warehouse,
        penaltyFactor: Double
    ): Map<String, Warehouse> {

        val shadowWarehousesById = allWarehouses.associate { warehouse ->
            warehouse.id to copyWarehouse(warehouse)
        }

        allWarehouses.forEach { warehouse ->
            warehouse.getOutgoingRoutes().forEach { route ->
                attachAdjustedRoute(route, bottleneckWarehouse, penaltyFactor, shadowWarehousesById)
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

    private fun attachAdjustedRoute(
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

        val shadowOrigin = shadowWarehousesById[route.originHub.id]
            ?: throw UseCaseException.WarehouseNotFound(route.originHub.id)

        val shadowDestination = shadowWarehousesById[route.destinationHub.id]
            ?: throw UseCaseException.WarehouseNotFound(route.destinationHub.id)

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
