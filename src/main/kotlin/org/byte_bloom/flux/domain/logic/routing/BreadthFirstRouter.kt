package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Warehouse

class BreadthFirstRouter {

    fun findLeastHopPath(start: Warehouse, destination: Warehouse): List<Warehouse> {
        if (start.id == destination.id) return listOf(start)

        val queue = ArrayDeque<Warehouse>()
        queue.add(start)

        val visited = mutableSetOf<String>()
        visited.add(start.id)

        val parents = mutableMapOf<String, Warehouse>()
        var destinationFound = false

        while (queue.isNotEmpty() && !destinationFound) {
            val current = queue.removeFirst()

            if (current.id == destination.id) {
                destinationFound = true
            } else {
                exploreNeighbors(current, visited, parents, queue)
            }
        }

        return if (destinationFound) buildPath(destination, parents) else emptyList()
    }

    private fun exploreNeighbors(
        current: Warehouse,
        visited: MutableSet<String>,
        parents: MutableMap<String, Warehouse>,
        queue: ArrayDeque<Warehouse>
    ) {
        val routes = current.getOutgoingRoutes()
        for (route in routes) {
            val neighbor = route.destinationHub
            if (!visited.contains(neighbor.id)) {
                visited.add(neighbor.id)
                parents[neighbor.id] = current
                queue.add(neighbor)
            }
        }
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
    }
}
