package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Warehouse

fun benchmarkRouters(
    warehouses: List<Warehouse>,
    bfsRouter: BreadthFirstRouter,
    bidirectionalRouter: FakeBidirectionalRouter   // 🔧 لاحقًا: نبدّلها بـ BidirectionalBfsRouter
) {
    val start = warehouses.first()
    val destination = warehouses.last()

    println("\n--- Benchmark: Standard BFS vs Bidirectional BFS ---")
    println("Route: ${start.id} → ${destination.id}")

    val bfsResult = bfsRouter.findLeastHopPath(start, destination)
    println(
        "Standard BFS:      ${bfsResult.path.size - 1} hops, " +
                "${bfsResult.nodesExplored} warehouses explored"
    )

    val bidirectionalResult = bidirectionalRouter.findPath(start, destination)
    println(
        "Bidirectional BFS: ${bidirectionalResult.path.size - 1} hops, " +
                "${bidirectionalResult.nodesExplored} warehouses explored"
    )

    printBenchmarkVerdict(bfsResult, bidirectionalResult)
}

private fun printBenchmarkVerdict(
    bfsResult: BidirectionalRoutingResult,
    bidirectionalResult: BidirectionalRoutingResult
) {
    val reduction = bfsResult.nodesExplored - bidirectionalResult.nodesExplored

    if (reduction > NO_REDUCTION) {
        val percentage = (reduction * PERCENT_MULTIPLIER) / bfsResult.nodesExplored
        println(
            "\n✅ Bidirectional BFS explored $reduction fewer warehouses " +
                    "(${"%.1f".format(percentage)}% reduction)."
        )
    } else {
        println("\nNote: no reduction observed — try a longer-distance warehouse pair.")
    }
}

private const val NO_REDUCTION = 0
private const val PERCENT_MULTIPLIER = 100.0
