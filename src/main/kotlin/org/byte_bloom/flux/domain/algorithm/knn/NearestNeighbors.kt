package org.byte_bloom.flux.domain.algorithm.knn

import org.byte_bloom.flux.domain.model.RegionalZone

data class LabeledPoint(
    val features: List<Double>,
    val sector: RegionalZone
)

data class Neighbor(
    val sector: RegionalZone,
    val distance: Double
)

fun findNearestNeighbors(
    query: List<Double>,
    trainingPoints: List<LabeledPoint>,
    k: Int
): List<Neighbor> {
    require(k > 0) { "k must be positive" }

    return trainingPoints
        .map { Neighbor(it.sector, euclideanDistance(query, it.features)) }
        .sortedBy { it.distance }
        .take(k)
}
