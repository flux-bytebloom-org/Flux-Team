package org.byte_bloom.flux.domain.response

import org.byte_bloom.flux.domain.model.Package

data class BottleneckCheckResult(
    val finalPackages: List<Package>,
    val bottleneckWarehouseId: String?,
    val totalPackagesConsidered: Int,
    val totalPackagesRerouted: Int,
    val rebalanceSummaries: List<RebalanceSummaryEntry>
)
