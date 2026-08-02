package org.byte_bloom.flux.domain.builder

import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.data.dataholders.VehicleRaw
import org.byte_bloom.flux.data.dataholders.WarehouseRaw
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.utils.logWarning

/**
 * Responsible for constructing the internal state of the logistics network.
 *
 * The builder converts raw data into domain objects and connects them
 * through their warehouse relationships.
 */
class DomainGraphBuilder {

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

    private fun buildWarehouses(
        warehouseRaws: List<WarehouseRaw>
    ): Map<String, Warehouse> {
        return warehouseRaws.associateBy(
            { it.id },
            valueTransform = { raw ->
                Warehouse(
                    id = raw.id,
                    name = raw.name,
                    regionalZone = raw.regionalZone,
                    latitude = raw.latitude,
                    longitude = raw.longitude
                )
            }
        )
    }

    private fun attachPackages(
        packages: List<PackageRaw>,
        warehouseMap: Map<String, Warehouse>
    ) {
        val packagesByDestination = packages.groupBy { it.destinationHubId }

        warehouseMap.forEach { (hubId, warehouse) ->
            packagesByDestination[hubId]?.forEach { raw ->
                val origin = warehouseMap[raw.originHubId]

                if (origin == null) {
                    logWarning("Package ${raw.id} references unknown origin hub, skipping")
                    return@forEach
                }

                warehouse.addPackage(
                    Package(
                        id = raw.id,
                        weight = raw.weight,
                        originHub = origin,
                        destinationHub = warehouse,
                        priority = raw.priority
                    )
                )
            }
        }
    }

    private fun attachVehicles(vehicles: List<VehicleRaw>, warehouseMap: Map<String, Warehouse>) {
        val vehiclesByHub = vehicles.groupBy { it.currentHubId }

        vehiclesByHub.forEach { (hubId, rawVehicles) ->
            val warehouse = warehouseMap[hubId]
            if (warehouse == null) {
                logWarning("Vehicles reference unknown hub $hubId, skipping ${rawVehicles.size} vehicle(s)")
                return@forEach
            }
            rawVehicles.forEach { raw ->
                warehouse.addVehicle(
                    Vehicle(
                        id = raw.id,
                        maxCapacityKg = raw.maxCapacityKg,
                        costPerKm = raw.costPerKm,
                        currentHub = warehouse
                    )
                )
            }
        }
    }

    private fun attachRoutes(
        routes: List<RouteRaw>,
        warehouseMap: Map<String, Warehouse>
    ) {
        val routesByOrigin = routes.groupBy { it.originHubId }

        warehouseMap.forEach { (hubId, warehouse) ->
            routesByOrigin[hubId]?.forEach { raw ->
                val destination = warehouseMap[raw.destinationHubId]

                if (destination == null) {
                    logWarning("Route ${raw.id} references unknown destination hub, skipping")
                    return@forEach
                }

                warehouse.addRoute(
                    Route(
                        id = raw.id,
                        distanceKm = raw.distanceKm,
                        typicalDelayMin = raw.typicalDelayMin,
                        originHub = warehouse,
                        destinationHub = destination
                    )
                )
            }
        }
    }

}
