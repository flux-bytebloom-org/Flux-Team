package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Warehouse

private const val SINGLE_NODE_PATH_SIZE = 1
private const val INITIAL_NODES_EXPLORED = 0
private const val EXPLORED_NODE_INCREMENT = 1

class BidirectionalBfsRouter(
    private val routes: List<Route>
) {
    private val incomingRoutes: Map<String, List<Warehouse>> by lazy {
        routes.groupBy { it.destinationHub.id }
            .mapValues { entry -> entry.value.map { it.originHub } }
    }

    fun findPath(start: Warehouse, destination: Warehouse): RoutingSearchResult {
        if (start.id == destination.id) {
            return RoutingSearchResult(path = listOf(start), nodesExplored = SINGLE_NODE_PATH_SIZE)
        }

        return executeSearch(SearchState(start, destination))
    }

    private fun executeSearch(state: SearchState): RoutingSearchResult {
        var totalNodesExplored = INITIAL_NODES_EXPLORED
        var resultPath: List<Warehouse> = emptyList()
        var pathFound = false

        while ((state.forwardQueue.isNotEmpty() || state.backwardQueue.isNotEmpty()) && !pathFound) {
            val (forwardIntersection, forwardNodes) = stepForward(state)
            totalNodesExplored += forwardNodes
            if (forwardIntersection != null) {
                resultPath = buildPath(forwardIntersection, state)
                pathFound = true
            } else {
                val (backwardIntersection, backwardNodes) = stepBackward(state)
                totalNodesExplored += backwardNodes
                if (backwardIntersection != null) {
                    resultPath = buildPath(backwardIntersection, state)
                    pathFound = true
                }
            }
        }

        return RoutingSearchResult(path = resultPath, nodesExplored = totalNodesExplored)
    }

    private fun stepForward(state: SearchState): StepResult {
        return expandFrontier(
            queue = state.forwardQueue,
            directionState = DirectionState(state.forwardVisited, state.backwardVisited, state.forwardParents)
        ) { warehouse -> warehouse.getOutgoingRoutes().map { it.destinationHub } }   // 🔧 getOutgoingRoutes()
    }

    private fun stepBackward(state: SearchState): StepResult {
        return expandFrontier(
            queue = state.backwardQueue,
            directionState = DirectionState(state.backwardVisited, state.forwardVisited, state.backwardParents)
        ) { warehouse -> incomingRoutes[warehouse.id] ?: emptyList() }
    }

    private fun expandFrontier(
        queue: ArrayDeque<Warehouse>,
        directionState: DirectionState,
        getNextNodes: (Warehouse) -> List<Warehouse>
    ): StepResult {
        val current = queue.removeFirstOrNull() ?: return StepResult(null, INITIAL_NODES_EXPLORED)
        val neighbors = getNextNodes(current)
        val intersection = processNeighbors(neighbors, current, directionState, queue)
        return StepResult(intersection, EXPLORED_NODE_INCREMENT)
    }

    private fun processNeighbors(
        neighbors: List<Warehouse>,
        current: Warehouse,
        directionState: DirectionState,
        queue: ArrayDeque<Warehouse>
    ): Warehouse? {
        var intersection: Warehouse? = null
        for (neighbor in neighbors) {
            if (directionState.visited.add(neighbor.id)) {
                directionState.parents[neighbor.id] = current
                queue.add(neighbor)
                if (intersection == null && directionState.otherVisited.contains(neighbor.id)) {
                    intersection = neighbor
                }
            }
        }
        return intersection
    }

    private fun buildPath(intersection: Warehouse, state: SearchState): List<Warehouse> {
        val forwardPath = buildForwardPath(intersection, state.start, state.forwardParents)
        val backwardPath = buildBackwardPath(intersection, state.destination, state.backwardParents)
        return forwardPath + backwardPath
    }

    private fun buildForwardPath(
        intersection: Warehouse,
        start: Warehouse,
        parents: Map<String, Warehouse>
    ): List<Warehouse> {
        return traceParents(intersection, start, parents).reversed()
    }

    private fun buildBackwardPath(
        intersection: Warehouse,
        destination: Warehouse,
        parents: Map<String, Warehouse>
    ): List<Warehouse> {
        val nextNode = parents[intersection.id] ?: return emptyList()
        return traceParents(nextNode, destination, parents)
    }

    private fun traceParents(
        current: Warehouse,
        target: Warehouse,
        parents: Map<String, Warehouse>
    ): List<Warehouse> {
        return generateSequence(current) { node ->
            if (node.id == target.id) null else parents[node.id]
        }.toList()
    }

    private data class StepResult(
        val intersection: Warehouse?,
        val nodesExploredIncrement: Int
    )

    private class DirectionState(
        val visited: MutableSet<String>,
        val otherVisited: Set<String>,
        val parents: MutableMap<String, Warehouse>
    )

    private class SearchState(
        val start: Warehouse,
        val destination: Warehouse
    ) {
        val forwardQueue = ArrayDeque<Warehouse>().apply { add(start) }
        val backwardQueue = ArrayDeque<Warehouse>().apply { add(destination) }
        val forwardVisited = mutableSetOf(start.id)
        val backwardVisited = mutableSetOf(destination.id)
        val forwardParents = mutableMapOf<String, Warehouse>()
        val backwardParents = mutableMapOf<String, Warehouse>()
    }
}

