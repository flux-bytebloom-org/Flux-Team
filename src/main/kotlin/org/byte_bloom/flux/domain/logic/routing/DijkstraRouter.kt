package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Warehouse

class DijkstraRouter {

    fun findShortestPath(start: Warehouse, destination: Warehouse): List<Warehouse> {
        if (start.id == destination.id) {
            return listOf(start)
        }

        val distances = mutableMapOf(start.id to 0.0)
        val parents = mutableMapOf<String, Warehouse>()
        val visited = mutableSetOf<String>()
        val discoveredWarehouses = mutableMapOf(start.id to start)

        while (true) {
            val current = findClosestUnvisitedWarehouse(
                discoveredWarehouses.values,
                distances,
                visited
            ) ?: break

            if (current.id == destination.id) {
                return buildPath(destination, parents)
            }

            visited.add(current.id)
            relaxNeighbors(current, distances, parents, visited, discoveredWarehouses)
        }

        return emptyList()
    }

    /**
     * Manually scans every undiscovered-but-not-yet-visited warehouse and
     * returns the one with the smallest known distance so far. This is the
     * handWritten replacement for what a PriorityQueue's poll() would do.
     */
    private fun findClosestUnvisitedWarehouse(
        warehouses: Collection<Warehouse>,
        distances: Map<String, Double>,
        visited: Set<String>
    ): Warehouse? {
        var closest: Warehouse? = null
        var closestDistance = Double.MAX_VALUE

        warehouses.forEach { warehouse ->
            if (warehouse.id !in visited) {
                val distance = distances[warehouse.id] ?: Double.MAX_VALUE
                if (distance < closestDistance) {
                    closestDistance = distance
                    closest = warehouse
                }
            }
        }

        return closest
    }

    private fun relaxNeighbors(
        current: Warehouse,
        distances: MutableMap<String, Double>,
        parents: MutableMap<String, Warehouse>,
        visited: Set<String>,
        discoveredWarehouses: MutableMap<String, Warehouse>
    ) {
        current.getOutgoingRoutes().forEach { route ->
            val neighbor = route.destinationHub
            if (neighbor.id in visited) return@forEach

            discoveredWarehouses[neighbor.id] = neighbor

            val newDistance = distances.getValue(current.id) + route.distanceKm
            val knownDistance = distances[neighbor.id] ?: Double.MAX_VALUE

            if (newDistance < knownDistance) {
                distances[neighbor.id] = newDistance
                parents[neighbor.id] = current
            }
        }
    }

    private fun buildPath(
        destination: Warehouse,
        parents: Map<String, Warehouse>
    ): List<Warehouse> {
        val parent = parents[destination.id] ?: return listOf(destination)
        return buildPath(parent, parents) + destination
    }
}
