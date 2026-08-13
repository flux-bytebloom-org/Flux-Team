package org.byte_bloom.flux.domain.builder

import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.dataholders.Priority
import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.data.dataholders.VehicleRaw
import org.byte_bloom.flux.data.dataholders.WarehouseRaw
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority as DomainPriority
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.ui.utils.logWarning

class DomainGraphBuilder(
    private val warehouseRepository: WarehouseRepository,
    private val packageRepository: PackageRepository,
    private val routeRepository: RouteRepository,
    private val vehicleRepository: VehicleRepository
) {

    fun buildGraph(): List<Warehouse> {
        val warehouses = warehouseRepository.getAll()
        val packages = packageRepository.getAll()
        val routes = routeRepository.getAll()
        val vehicles = vehicleRepository.getAll()

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
        packages.forEach { raw ->
            val origin = warehouseMap[raw.originHubId]
            val destination = warehouseMap[raw.destinationHubId]

            if (origin == null || destination == null) {
                logWarning("Package ${raw.id} references unknown warehouse, skipping")
                return@forEach
            }

            destination.addPackage(
                Package(
                    id = raw.id,
                    weight = raw.weight,
                    originHub = origin,
                    destinationHub = destination,
                    priority = mapPriority(raw.priority)
                )
            )
        }
    }

    private fun attachVehicles(
        vehicles: List<VehicleRaw>,
        warehouseMap: Map<String, Warehouse>
    ) {
        vehicles.forEach { raw ->
            val warehouse = warehouseMap[raw.currentHubId]

            if (warehouse == null) {
                logWarning("Vehicle ${raw.id} references unknown hub, skipping")
                return@forEach
            }

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

    private fun attachRoutes(
        routes: List<RouteRaw>,
        warehouseMap: Map<String, Warehouse>
    ) {
        routes.forEach { raw ->
            val origin = warehouseMap[raw.originHubId]
            val destination = warehouseMap[raw.destinationHubId]

            if (origin == null || destination == null) {
                logWarning("Route ${raw.id} references unknown warehouse, skipping")
                return@forEach
            }

            origin.addRoute(
                Route(
                    id = raw.id,
                    distanceKm = raw.distanceKm,
                    typicalDelayMin = raw.typicalDelayMin,
                    originHub = origin,
                    destinationHub = destination
                )
            )
        }
    }

    private fun mapPriority(raw: Priority): DomainPriority {
        return when (raw) {
            Priority.LOW -> DomainPriority.LOW
            Priority.STANDARD -> DomainPriority.STANDARD
            Priority.URGENT -> DomainPriority.URGENT
        }
    }
}