package org.byte_bloom.flux.domain.builder

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.ui.utils.logWarning

class DomainGraphBuilder(
    val warehouseRepository: WarehouseRepository,
    val packageRepository: PackageRepository,
    val routeRepository: RouteRepository,
    val vehicleRepository: VehicleRepository
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
        warehouses: List<Warehouse>
    ): Map<String, Warehouse> {
        return warehouses.associateBy { it.id }
    }

    private fun attachPackages(
        packages: List<Package>,
        warehouseMap: Map<String, Warehouse>
    ) {
        packages.forEach { pkg ->
            val origin = warehouseMap[pkg.originHub.id]
            val destination = warehouseMap[pkg.destinationHub.id]

            if (origin == null || destination == null) {
                logWarning("Package ${pkg.id} references unknown warehouse, skipping")
                return@forEach
            }

            destination.addPackage(
                Package(
                    id = pkg.id,
                    weight = pkg.weight,
                    originHub = origin,
                    destinationHub = destination,
                    priority = pkg.priority
                )
            )
        }
    }

    private fun attachVehicles(
        vehicles: List<Vehicle>,
        warehouseMap: Map<String, Warehouse>
    ) {
        vehicles.forEach { vehicle ->
            val warehouse = warehouseMap[vehicle.currentHub.id]

            if (warehouse == null) {
                logWarning("Vehicle ${vehicle.id} references unknown hub, skipping")
                return@forEach
            }

            warehouse.addVehicle(
                Vehicle(
                    id = vehicle.id,
                    maxCapacityKg = vehicle.maxCapacityKg,
                    costPerKm = vehicle.costPerKm,
                    currentHub = warehouse
                )
            )
        }
    }

    private fun attachRoutes(
        routes: List<Route>,
        warehouseMap: Map<String, Warehouse>
    ) {
        routes.forEach { route ->
            val origin = warehouseMap[route.originHub.id]
            val destination = warehouseMap[route.destinationHub.id]

            if (origin == null || destination == null) {
                logWarning("Route ${route.id} references unknown warehouse, skipping")
                return@forEach
            }

            origin.addRoute(
                Route(
                    id = route.id,
                    distanceKm = route.distanceKm,
                    typicalDelayMin = route.typicalDelayMin,
                    originHub = origin,
                    destinationHub = destination
                )
            )
        }
    }
}
