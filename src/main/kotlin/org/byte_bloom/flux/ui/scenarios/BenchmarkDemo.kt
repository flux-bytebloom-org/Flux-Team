package org.byte_bloom.flux.ui.scenarios

import org.byte_bloom.flux.domain.algorithm.routing.BidirectionalBfsRouter
import org.byte_bloom.flux.domain.algorithm.routing.BreadthFirstRouter
import org.byte_bloom.flux.domain.algorithm.routing.RoutingSearchResult
import org.byte_bloom.flux.domain.algorithm.routing.findLongDistancePair
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Warehouse
import org.koin.java.KoinJavaComponent.getKoin

private const val NO_REDUCTION = 0
private const val PERCENT_MULTIPLIER = 100.0


fun benchmarkRouters(
    warehouses: List<Warehouse>,
    routes : List<Route>
) {

    val bfsRouter: BreadthFirstRouter = getKoin().get()
    val bidirectionalRouter: BidirectionalBfsRouter = getKoin().get()

    val (start, destination) = findLongDistancePair(warehouses, bfsRouter)
    println("\n\nfarthest pair of warehouses: ${start.id} → ${destination.id}")

    println("\n--- Benchmark: Standard BFS vs Bidirectional BFS ---")
    println("Route: ${start.id} → ${destination.id}")

    val bfsResult = bfsRouter.findLeastHopPath(start, destination)
    val bidirectionalResult = bidirectionalRouter.findPath(start, destination, routes)

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
