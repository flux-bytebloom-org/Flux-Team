package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Warehouse

private const val NO_REDUCTION = 0
private const val PERCENT_MULTIPLIER = 100.0


fun benchmarkRouters(
    warehouses: List<Warehouse>,
    bfsRouter: BreadthFirstRouter,
    bidirectionalRouter: BidirectionalBfsRouter   // 🔧 لاحقًا: نبدّلها بـ BidirectionalBfsRouter
) {

    val (start, destination) = findLongDistancePair(warehouses, bfsRouter)
    println("\n\nfarthest pair of warehouses: ${start.id} → ${destination.id}")




    println("\n--- Benchmark: Standard BFS vs Bidirectional BFS ---")
    println("Route: ${start.id} → ${destination.id}")

    val bfsResult = bfsRouter.findLeastHopPath(start, destination)
    val bidirectionalResult = bidirectionalRouter.findPath(start, destination)

    if (bfsResult.path.isEmpty() || bidirectionalResult.path.isEmpty()) {
        println("⚠️ No path found between ${start.id} and ${destination.id} — skipping benchmark.")
        return
    }

    println(
        "Standard BFS:      ${bfsResult.path.size - 1} hops, " +
                "${bfsResult.nodesExplored} warehouses explored"
    )

    println(
        "Bidirectional BFS: ${bidirectionalResult.path.size - 1} hops, " +
                "${bidirectionalResult.nodesExplored} warehouses explored"
    )

    printBenchmarkVerdict(bfsResult, bidirectionalResult)
}

private fun printBenchmarkVerdict(
    bfsResult: RoutingSearchResult,
    bidirectionalResult: RoutingSearchResult
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


