package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Warehouse

private const val FAKE_NODES_EXPLORED = 40

/**
 * TEMPORARY stub — replace with the real BidirectionalBfsRouter
 * once it's ready (same method signature, so swapping is a one-line change).
 */
class FakeBidirectionalRouter {
    fun findPath(start: Warehouse, destination: Warehouse): RoutingSearchResult {
        // بيانات وهمية ثابتة، بس عشان نجرب شكل الطباعة والربط
        return RoutingSearchResult(
            path = listOf(start, destination),
            nodesExplored = FAKE_NODES_EXPLORED
        )
    }


}
