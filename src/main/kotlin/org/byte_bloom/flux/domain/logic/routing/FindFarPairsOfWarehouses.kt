package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Warehouse

/**
 * Finds a long-distance warehouse pair using a double-sweep BFS:
 * first finds the farthest warehouse from an arbitrary anchor, then
 * the farthest warehouse from that result. This approximates the
 * network's diameter, ensuring the benchmark compares BFS vs
 * Bidirectional BFS on a genuinely long path rather than a random
 * (possibly short) pair.
 */

fun findLongDistancePair(
    warehouses: List<Warehouse>,
    bfsRouter: BreadthFirstRouter
): Pair<Warehouse, Warehouse> {
    val anchor = warehouses.first()

    val farthestFromAnchor = warehouses
        .filter { it.id != anchor.id }
        .maxByOrNull { candidate ->
            bfsRouter.findLeastHopPath(anchor, candidate).path.size
        }
        ?: anchor

    val farthestFromFarthest = warehouses
        .filter { it.id != farthestFromAnchor.id }
        .maxByOrNull { candidate ->
            bfsRouter.findLeastHopPath(farthestFromAnchor, candidate).path.size
        }
        ?: farthestFromAnchor

    return farthestFromFarthest to farthestFromAnchor
}

