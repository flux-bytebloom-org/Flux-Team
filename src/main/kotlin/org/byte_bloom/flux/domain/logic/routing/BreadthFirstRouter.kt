package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Warehouse
import java.util.ArrayDeque

class BreadthFirstRouter {

    fun findLeastHopPath(
        start: Warehouse,
        destination: Warehouse
    ): List<Warehouse> {

        if (start.id == destination.id) {
            return listOf(start)
        }

        val queue = ArrayDeque<List<Warehouse>>()
        val visited = mutableSetOf<String>()

        queue.addLast(listOf(start))
        visited.add(start.id)

        while (queue.isNotEmpty()) {

            val currentPath = queue.removeFirst()
            val currentWarehouse = currentPath.last()

            for (route in currentWarehouse.getOutgoingRoutes()) {

                val nextWarehouse = route.destinationHub

                if (nextWarehouse.id in visited) {
                    continue
                }

                val newPath = currentPath + nextWarehouse

                if (nextWarehouse.id == destination.id) {
                    return newPath
                }

                visited.add(nextWarehouse.id)
                queue.addLast(newPath)
            }
        }

        return emptyList()
    }
}