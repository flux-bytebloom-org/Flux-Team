package org.byte_bloom.flux.domain.algorithm.knn

import kotlin.math.pow
import kotlin.math.sqrt

fun euclideanDistance(a: List<Double>, b: List<Double>): Double {
    require(a.size == b.size) { "Points must have the same number of dimensions" }
    return sqrt(a.zip(b).sumOf { (x, y) -> (x - y).pow(2) })
}