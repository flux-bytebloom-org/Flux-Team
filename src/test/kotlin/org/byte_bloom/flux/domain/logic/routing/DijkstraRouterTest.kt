package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Warehouse

class DijkstraRouterTest {

    fun testFindsLowestTotalDistancePath() {
        val (a, b, c, d) = createWarehouses("A", "B", "C", "D")

        a.addRoute(createRoute("R1", a, b, 100.0))
        b.addRoute(createRoute("R2", b, d, 100.0))
        a.addRoute(createRoute("R3", a, c, 10.0))
        c.addRoute(createRoute("R4", c, d, 10.0))

        assertPath(a, d, listOf("A", "C", "D"))
    }

    fun testChoosesShorterDistancePathOverBFS() {
        val (a, b, c, e, d) = createWarehouses("A", "B", "C", "E", "D")

        a.addRoute(createRoute("R1", a, b, 100.0))
        b.addRoute(createRoute("R2", b, d, 100.0))
        a.addRoute(createRoute("R3", a, c, 10.0))
        c.addRoute(createRoute("R4", c, e, 10.0))
        e.addRoute(createRoute("R5", e, d, 10.0))

        assertBfsPath(a, d, listOf("A", "B", "D"))
        assertPath(a, d, listOf("A", "C", "E", "D"))
    }

    fun testReturnsEmptyPathWhenUnreachable() {
        val (a, isolated) = createWarehouses("A", "Z")
        assertPath(a, isolated, emptyList())
    }

    fun testDoesNotLoopForeverWithCycle() {
        val (a, b, c) = createWarehouses("A", "B", "C")

        a.addRoute(createRoute("R1", a, b, 5.0))
        b.addRoute(createRoute("R2", b, a, 5.0))
        b.addRoute(createRoute("R3", b, c, 5.0))

        assertPath(a, c, listOf("A", "B", "C"))
    }


    private fun assertPath(from: Warehouse, to: Warehouse, expectedIds: List<String>) {
        val actualPath = DijkstraRouter().findShortestPath(from, to).map { it.id }
        check(actualPath == expectedIds) { "Dijkstra Failed! Expected $expectedIds but got $actualPath" }
    }

    private fun assertBfsPath(from: Warehouse, to: Warehouse, expectedIds: List<String>) {
        val actualPath = BreadthFirstRouter().findLeastHopPath(from, to).map { it.id }
        check(actualPath == expectedIds) { "BFS Failed! Expected $expectedIds but got $actualPath" }
    }

    private fun createWarehouses(vararg ids: String): List<Warehouse> {
        return ids.map { Warehouse(id = it, name = "Warehouse $it", regionalZone = "Test Zone", latitude = 0.0, longitude = 0.0) }
    }

    private fun createRoute(id: String, origin: Warehouse, dest: Warehouse, distanceKm: Double): Route {
        return Route(id = id, originHub = origin, destinationHub = dest, distanceKm = distanceKm, typicalDelayMin = 0.0)
    }
}
