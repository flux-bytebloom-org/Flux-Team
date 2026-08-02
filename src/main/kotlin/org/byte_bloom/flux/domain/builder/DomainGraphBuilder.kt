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

    fun buildGraph(
        warehouses: List<WarehouseRaw>,
        packages: List<PackageRaw>,
        routes: List<RouteRaw>,
        vehicles: List<VehicleRaw>
    ): List<Warehouse> {

        // Build domain warehouses and index them by ID
        // to allow O(1) constant-time lookups.
        val warehouseMap = buildWarehouses(warehouses)

        // Distribute packages to their respective destination hubs.
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

        // Attach vehicles to their current location.
        vehicles.forEach { vehicle ->
            warehouseMap[vehicle.currentHub]?.addVehicle(
                Vehicle(
                   id= vehicle.id,
                    maxCapacityKg=vehicle.maxCapacityKg,
                    costPerKm= vehicle.costPerKm,
                    currentHub= warehouseMap[vehicle.currentHub]!!
                )
            )
        }

        // Attach outgoing routes to their origin warehouses.
        routes.forEach { route ->
            warehouseMap[route.originHub]?.addRoute(
                Route(
                    id= route.id,
                    distanceKm = route.distanceKm,
                    typicalDelayMin= route.typicalDelayMin,
                    originHub= warehouseMap[route.originHub]!!,
                    destinationHub= warehouseMap[route.destinationHub]!!
                )
            )
        }

        return warehouseMap.values.toList()
    }
}
