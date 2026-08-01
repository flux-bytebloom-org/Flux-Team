package org.byte_bloom.flux.builder

import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle

/**
 * Responsible for constructing the internal state of the logistics network.
 * By mapping domain entities to their respective hubs, this builder ensures
 * that warehouses have full context of available packages, vehicles, and routes.
 */
class DomainGraphBuilder {

    fun buildGraph(
        warehouses: List<Warehouse>,
        packages: List<Package>,
        routes: List<Route>,
        vehicles: List<Vehicle>
    ): List<Warehouse> {

        // Indexing warehouses by ID to allow O(1) constant-time lookups,
        // avoiding nested loops which would degrade performance as data grows.
        val warehouseMap = warehouses.associateBy { it.warehouseId }

        // Distribute packages to their respective destination hubs to ensure
        // the warehouse model correctly reflects current inventory demands.
        packages.forEach { pkg ->
            warehouseMap[pkg.destinationHubId]?.cargoQueue?.add(pkg)
        }

        // Attach vehicles to their current location to maintain an accurate
        // state of available transport resources at each specific node.
        vehicles.forEach { vehicle ->
            warehouseMap[vehicle.currentHub]?.stationedVehicles?.add(vehicle)
        }

        // Configure network topography by linking origin hubs to their allowed
        // outgoing routes, enabling pathfinding algorithms to traverse the graph.
        routes.forEach { route ->
            warehouseMap[route.originHub]?.outgoingRoutes?.add(route)
        }

        return warehouses

    }

}