package org.byte_bloom.flux.domain.algorithm.knn

import org.byte_bloom.flux.domain.model.RegionalZone

fun majorityVote(neighbors: List<Neighbor>): RegionalZone {
    require(neighbors.isNotEmpty()) { "Cannot vote without neighbors" }

    return neighbors
        .groupBy { it.sector }
        .maxWith(
            compareBy<Map.Entry<RegionalZone, List<Neighbor>>> { it.value.size }
                .thenByDescending { entry -> entry.value.minOf { it.distance } }
        )
        .key
}
