package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Warehouse

fun testRoutingComparison(
    warehouses: List<Warehouse>,
    bfsRouter: BreadthFirstRouter,
    dijkstraRouter: DijkstraRouter
) {
    val start = warehouses.first()
    val destination = warehouses.last()

    println("\n--- Routing Comparison: BFS vs Dijkstra ---")

    val bfsResult = bfsRouter.findLeastHopPath(start, destination)
    val bfsPath = bfsResult.path
    val bfsDistance = calculatePathDistance(bfsPath)
    println("BFS (least hops): ${bfsPath.map { it.id }} — ${bfsPath.size - 1} hops, $bfsDistance km")

    val dijkstraPath = dijkstraRouter.findShortestPath(start, destination)
    val dijkstraDistance = calculatePathDistance(dijkstraPath)
    println("Dijkstra (shortest distance): ${dijkstraPath.map { it.id }} — " +
            "${dijkstraPath.size - 1} hops, $dijkstraDistance km")

    printComparisonVerdict(bfsPath, dijkstraPath, bfsDistance, dijkstraDistance)
}

private fun calculatePathDistance(path: List<Warehouse>): Double {
    return path.zipWithNext().sumOf { (a, b) ->
        a.getOutgoingRoutes().first { it.destinationHub.id == b.id }.distanceKm
    }
}

private fun printComparisonVerdict(
    bfsPath: List<Warehouse>,
    dijkstraPath: List<Warehouse>,
    bfsDistance: Double,
    dijkstraDistance: Double
) {
    if (bfsPath == dijkstraPath) {
        println("\nNote: both algorithms picked the same path here — try a different " +
                "warehouse pair to see the difference.")
        return
    }

    println(
        "\n⚠️ BFS is NOT always the best choice: it minimizes transfer hops, " +
                "not physical distance."
    )

    if (bfsDistance > dijkstraDistance) {
        val extra = bfsDistance - dijkstraDistance
        println(
            "In this case, BFS's path is ${"%.1f".format(extra)} km longer than " +
                    "Dijkstra's — fewer hops does not mean a shorter route."
        )
    }
}
