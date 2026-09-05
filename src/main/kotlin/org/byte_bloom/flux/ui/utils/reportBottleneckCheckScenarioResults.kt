package org.byte_bloom.flux.ui.utils

import org.byte_bloom.flux.domain.response.BottleneckCheckResult

fun printBottleneckReport(result: BottleneckCheckResult) {
    println("\n=== Bottleneck Rebalance Report ===")

    if (result.bottleneckWarehouseId == null) {
        println("No bottleneck detected — no rebalancing needed.")
        return
    }

    println("Bottleneck warehouse: ${result.bottleneckWarehouseId}")
    println("Packages considered: ${result.totalPackagesConsidered}")
    println("Packages rerouted: ${result.totalPackagesRerouted}")
    println()

    result.rebalanceSummaries.forEach { entry ->
        val distanceNote = if (entry.newDistanceKm > entry.oldDistanceKm) {
            " (longer by ${"%.1f".format(entry.newDistanceKm - entry.oldDistanceKm)} km)"
        } else {
            " (shorter by ${"%.1f".format(entry.oldDistanceKm - entry.newDistanceKm)} km)"
        }

        println(
            "${entry.originId} -> ${entry.destinationId}: ${entry.packagesRerouted} package(s) rerouted$distanceNote"
        )
        println("   old path: ${entry.oldPath.joinToString(" -> ")}  (${"%.1f".format(entry.oldDistanceKm)} km)")
        println("   new path: ${entry.newPath.joinToString(" -> ")}  (${"%.1f".format(entry.newDistanceKm)} km)\n")
    }
}
