package org.byte_bloom.flux.domain.response

data class RebalanceSummaryEntry(
    val originId: String,
    val destinationId: String,
    val packagesRerouted: Int,
    val oldPath: List<String>,
    val newPath: List<String>,
    val oldDistanceKm: Double,
    val newDistanceKm: Double
)
