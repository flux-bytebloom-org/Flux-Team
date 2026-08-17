package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Warehouse

class DijkstraRouter {

    fun findShortestPath(start: Warehouse, destination: Warehouse): List<Warehouse> {
        if (start.id == destination.id) return listOf(start)

        val distances = mutableMapOf<String, Double>()
        distances[start.id] = INITIAL_DISTANCE

        val parents = mutableMapOf<String, Warehouse>()
        val visited = mutableSetOf<String>()
        val discovered = mutableMapOf<String, Warehouse>()
        discovered[start.id] = start

        var current = findMinDistanceNode(discovered, distances, visited)

        while (current != null && current.id != destination.id) {
            visited.add(current.id)
            relaxNeighbors(current, distances, parents, visited, discovered)
            current = findMinDistanceNode(discovered, distances, visited)
        }

        val isReached = current != null && current.id == destination.id
        return if (isReached) buildPath(destination, parents) else emptyList()
    }

    private fun relaxNeighbors(
        current: Warehouse,
        distances: MutableMap<String, Double>,
        parents: MutableMap<String, Warehouse>,
        visited: Set<String>,
        discovered: MutableMap<String, Warehouse>
    ) {
        val currentDist = distances[current.id] ?: return
        val routes = current.getOutgoingRoutes()

        for (route in routes) {
            val neighbor = route.destinationHub

            if (!visited.contains(neighbor.id)) {
                discovered[neighbor.id] = neighbor
                val newDist = currentDist + route.distanceKm
                val existingDist = distances[neighbor.id] ?: Double.MAX_VALUE

                if (newDist < existingDist) {
                    distances[neighbor.id] = newDist
                    parents[neighbor.id] = current
                }
            }
        }
    }

    private fun findMinDistanceNode(
        discovered: Map<String, Warehouse>,
        distances: Map<String, Double>,
        visited: Set<String>
    ): Warehouse? {
        var minNode: Warehouse? = null
        var minDistance = Double.MAX_VALUE

        val warehouses = discovered.values
        for (node in warehouses) {
            if (!visited.contains(node.id)) {
                val dist = distances[node.id] ?: Double.MAX_VALUE
                if (dist < minDistance) {
                    minDistance = dist
                    minNode = node
                }
            }
        }

        return minNode
    }

    private fun buildPath(destination: Warehouse, parents: Map<String, Warehouse>): List<Warehouse> {
        val path = mutableListOf<Warehouse>()
        var current: Warehouse? = destination

        while (current != null) {
            path.add(START_INDEX, current)
            current = parents[current.id]
        }

        return path
    }

    companion object {
        private const val START_INDEX = 0
        private const val INITIAL_DISTANCE = 0.0
    }
}
