package org.byte_bloom.flux.domain.builder

import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.data.dataholders.VehicleRaw
import org.byte_bloom.flux.data.dataholders.WarehouseRaw
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

/**
 * Responsible for constructing the internal state of the logistics network.
 *
 * The builder converts raw data into domain objects and connects them
 * through their warehouse relationships.
 */
class DomainGraphBuilder {

    private fun buildWarehouses(
        warehouseRaws: List<WarehouseRaw>
    ): Map<String, Warehouse> {
        return warehouseRaws.associateBy(
            keySelector = { it.id },
            valueTransform = { raw ->
                Warehouse(
                    raw.id,
                    raw.name,
                    raw.regionalZone,
                    raw.longitude,
                    raw.latitude
                )
            }
        )
    }

    private fun attachPackages(
        packages: List<PackageRaw>,
        warehouseMap: Map<String, Warehouse>
    ) {
        packages.forEach { pkg ->
            warehouseMap[pkg.destinationHubId]?.addPackage(
                Package(
                    pkg.id,
                    pkg.weight,
                    warehouseMap[pkg.originHubId]!!,
                    warehouseMap[pkg.destinationHubId]!!,
                    pkg.priority
                )
            )
        }
    }

    private fun attachVehicles(
        vehicles: List<VehicleRaw>,
        warehouseMap: Map<String, Warehouse>
    ) {
        vehicles.forEach { vehicle ->
            warehouseMap[vehicle.currentHub]?.addVehicle(
                Vehicle(
                    id = vehicle.id,
                    maxCapacityKg = vehicle.maxCapacityKg,
                    costPerKm = vehicle.costPerKm,
                    currentHub = warehouseMap[vehicle.currentHub]!!
                )
            )
        }
    }

    private fun attachRoutes(
        routes: List<RouteRaw>,
        warehouseMap: Map<String, Warehouse>
    ) {
        routes.forEach { route ->
            warehouseMap[route.originHub]?.addRoute(
                Route(
                    id = route.id,
                    distanceKm = route.distanceKm,
                    typicalDelayMin = route.typicalDelayMin,
                    originHub = warehouseMap[route.originHub]!!,
                    destinationHub = warehouseMap[route.destinationHub]!!
                )
            )
        }
    }

    fun buildGraph(
        warehouses: List<WarehouseRaw>,
        packages: List<PackageRaw>,
        routes: List<RouteRaw>,
        vehicles: List<VehicleRaw>
    ): List<Warehouse> {
        val warehouseMap = buildWarehouses(warehouses)

        attachPackages(packages, warehouseMap)
        attachVehicles(vehicles, warehouseMap)
        attachRoutes(routes, warehouseMap)

        return warehouseMap.values.toList()
    }
}
