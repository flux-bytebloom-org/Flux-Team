package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Warehouse
import kotlin.collections.map

class DijkstraRouterTest {

    fun testFindsLowestTotalDistancePath() {
        val w = createWarehouses("A", "B", "C", "D", "E")

        w[0].addRoute(createRoute("R1", w[0], w[1], DISTANCE_LONG))
        w[1].addRoute(createRoute("R2", w[1], w[3], DISTANCE_LONG))
        w[0].addRoute(createRoute("R3", w[0], w[2], DISTANCE_SHORT))
        w[2].addRoute(createRoute("R4", w[2], w[3], DISTANCE_SHORT))

        assertPath(w[0], w[3], listOf("A", "C", "D"))
    }

    fun testChoosesShorterDistancePathOverBFS() {
        val w = createWarehouses("A", "B", "C", "E", "D")

        w[0].addRoute(createRoute("R1", w[0], w[1], DISTANCE_LONG))
        w[1].addRoute(createRoute("R2", w[1], w[4], DISTANCE_LONG))
        w[0].addRoute(createRoute("R3", w[0], w[2], DISTANCE_SHORT))
        w[2].addRoute(createRoute("R4", w[2], w[3], DISTANCE_SHORT))
        w[3].addRoute(createRoute("R5", w[3], w[4], DISTANCE_SHORT))

        assertBfsPath(w[0], w[4], listOf("A", "B", "D"))
        assertPath(w[0], w[4], listOf("A", "C", "E", "D"))
    }

    fun testReturnsEmptyPathWhenUnreachable() {
        val w = createWarehouses("A", "Z")
        assertPath(w[0], w[1], emptyList())
    }

    fun testDoesNotLoopForeverWithCycle() {
        val w = createWarehouses("A", "B", "C")

        w[0].addRoute(createRoute("R1", w[0], w[1], DISTANCE_MEDIUM))
        w[1].addRoute(createRoute("R2", w[1], w[0], DISTANCE_MEDIUM))
        w[1].addRoute(createRoute("R3", w[1], w[2], DISTANCE_MEDIUM))

        assertPath(w[0], w[2], listOf("A", "B", "C"))
    }

    private fun assertPath(from: Warehouse, to: Warehouse, expected: List<String>) {
        val actual = DijkstraRouter().findShortestPath(from, to, RoutingCriterion.DISTANCE).map { it.id }
        check(actual == expected) { "Dijkstra Failed! Expected $expected but got $actual" }
    }

    private fun assertBfsPath(from: Warehouse, to: Warehouse, expected: List<String>) {
        //val actual = BreadthFirstRouter().findLeastHopPath(from, to).map { it.id }
        val actual = (BreadthFirstRouter().findLeastHopPath(start = from, destination = to)).path.map { it.id }
        check(actual == expected) { "BFS Failed! Expected $expected but got $actual" }
    }

    private fun createWarehouses(vararg ids: String) = ids.map { id ->
        Warehouse(id = id, name = "Warehouse $id", regionalZone = "Test Zone", latitude = 0.0, longitude = 0.0)
    }

    private fun createRoute(id: String, origin: Warehouse, dest: Warehouse, dist: Double) = Route(
        id = id,
        originHub = origin,
        destinationHub = dest,
        distanceKm = dist,
        typicalDelayMin = 0.0
    )

    companion object {
        private const val DISTANCE_LONG = 100.0
        private const val DISTANCE_MEDIUM = 5.0
        private const val DISTANCE_SHORT = 10.0
    }
}

fun main() {
    val runner = DijkstraRouterTest()
    runner.testFindsLowestTotalDistancePath()
    runner.testChoosesShorterDistancePathOverBFS()
    runner.testReturnsEmptyPathWhenUnreachable()
    runner.testDoesNotLoopForeverWithCycle()
    println("All DijkstraRouter tests passed successfully!")
}
