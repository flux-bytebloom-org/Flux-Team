package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Warehouse

/**
 * Why BFS fails to find the shortest-distance path:
 *
 * BFS treats every edge (Route) as having equal weight, so it finds the
 * path with the fewest transfer hops — not the path with the smallest
 * cumulative distance. Dijkstra's algorithm accounts for the actual
 * edge weights, so it correctly finds the minimum-cost path regardless
 * of how many hops it takes.
 *
 * Enhanced Functionality:
 * This router has been expanded to accept routing criteria parameters (RoutingCriterion),
 * allowing it to optimize paths based on different metrics such as distance (distanceKm)
 * or time delay (typicalDelayMin) dynamically.
 *
 * See testRoutingComparison() in Main.kt for a live example using
 * real warehouse data where the algorithms compare routing strategies.
 */

enum class RoutingCriterion {
    DISTANCE,
    TIME_DELAY
}

private const val START_INDEX = 0
private const val INITIAL_COST = 0.0

class DijkstraRouter {

    fun findShortestPath(start: Warehouse, destination: Warehouse,criterion: RoutingCriterion): List<Warehouse> {
        if (start.id == destination.id) return listOf(start)

        val costs = mutableMapOf<String, Double>()
        costs[start.id] = INITIAL_COST

        val parents = mutableMapOf<String, Warehouse>()
        val visited = mutableSetOf<String>()
        val discovered = mutableMapOf<String, Warehouse>()
        discovered[start.id] = start

        var current = findMinDistanceNode(discovered, costs, visited)

        while (current != null && current.id != destination.id) {
            visited.add(current.id)
            relaxNeighbors(current, costs, parents, visited, discovered, criterion)
            current = findMinDistanceNode(discovered, costs, visited)
        }

        val isReached = current != null && current.id == destination.id
        return if (isReached) buildPath(destination, parents) else emptyList()
    }

    private fun relaxNeighbors(
        current: Warehouse,
        costs: MutableMap<String, Double>,
        parents: MutableMap<String, Warehouse>,
        visited: Set<String>,
        discovered: MutableMap<String, Warehouse>,
        criterion: RoutingCriterion
    ) {
        val currentCost = costs[current.id] ?: return
        val routes = current.getOutgoingRoutes()

        for (route in routes) {
            val neighbor = route.destinationHub

            if (!visited.contains(neighbor.id)) {
                discovered[neighbor.id] = neighbor

                val edgeWeight = getRouteWeight(route, criterion)

                val newCost = currentCost + edgeWeight
                val existingCost = costs[neighbor.id] ?: Double.MAX_VALUE

                if (newCost < existingCost) {
                    costs[neighbor.id] = newCost
                    parents[neighbor.id] = current
                }
            }
        }
    }

    private fun getRouteWeight(route: Route, criterion: RoutingCriterion): Double {
        return when (criterion) {
            RoutingCriterion.DISTANCE -> route.distanceKm
            RoutingCriterion.TIME_DELAY -> route.typicalDelayMin
        }
    }

    private fun findMinDistanceNode(
        discovered: Map<String, Warehouse>,
        costs: Map<String, Double>,
        visited: Set<String>
    ): Warehouse? {
        var minNode: Warehouse? = null
        var minCost = Double.MAX_VALUE

        val warehouses = discovered.values
        for (node in warehouses) {
            if (!visited.contains(node.id)) {
                val cost = costs[node.id] ?: Double.MAX_VALUE
                if (cost < minCost) {
                    minCost = cost
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

}

