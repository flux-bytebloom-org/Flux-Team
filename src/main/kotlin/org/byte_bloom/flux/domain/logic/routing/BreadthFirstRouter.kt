package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Warehouse

private const val SINGLE_NODE_COUNT = 1

class BreadthFirstRouter {

    fun findLeastHopPath(
        start: Warehouse,
        destination: Warehouse
    ): BidirectionalRoutingResult  {

        if (start.id == destination.id) {
            return BidirectionalRoutingResult(listOf(start), SINGLE_NODE_COUNT)
        }

        val queue = ArrayDeque<List<Warehouse>>()
        val visited = mutableSetOf<String>()

        queue.addLast(listOf(start))
        visited.add(start.id)

        var foundPath: List<Warehouse>? = null

        while (queue.isNotEmpty() && foundPath == null) {
            val currentPath = queue.removeFirst()
            val currentWarehouse = currentPath.last()

            foundPath = processRoutes(
                currentWarehouse,
                destination,
                currentPath,
                visited,
                queue
            )
        }

        return BidirectionalRoutingResult(
            path = foundPath ?: emptyList(),
            nodesExplored = visited.size
        )
    }

    private fun processRoutes(
        currentWarehouse: Warehouse,
        destination: Warehouse,
        currentPath: List<Warehouse>,
        visited: MutableSet<String>,
        queue: ArrayDeque<List<Warehouse>>
    ): List<Warehouse>? {

        for (route in currentWarehouse.getOutgoingRoutes()) {
            val nextWarehouse = route.destinationHub

            if (nextWarehouse.id !in visited) {
                val newPath = currentPath + nextWarehouse

                if (nextWarehouse.id == destination.id) {
                    visited.add(nextWarehouse.id)
                    return newPath
                }

                visited.add(nextWarehouse.id)
                queue.addLast(newPath)
            }
        }

        return null
    }

}
