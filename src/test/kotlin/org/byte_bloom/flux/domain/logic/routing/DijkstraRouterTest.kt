package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Warehouse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DijkstraRouterTest {

    @Test
    fun `finds the lowest total distance path`() {
        val a = createWarehouse("A")
        val b = createWarehouse("B")
        val c = createWarehouse("C")
        val d = createWarehouse("D")

        // Path A -> B -> D = 100 + 100 = 200
        a.addRoute(createRoute("R1", a, b, distanceKm = 100.0))
        b.addRoute(createRoute("R2", b, d, distanceKm = 100.0))

        // Path A -> C -> D = 10 + 10 = 20 (shorter total distance, but same 2 hops)
        a.addRoute(createRoute("R3", a, c, distanceKm = 10.0))
        c.addRoute(createRoute("R4", c, d, distanceKm = 10.0))

        val path = DijkstraRouter().findShortestPath(a, d)

        assertEquals(listOf("A", "C", "D"), path.map { it.id })
    }

    @Test
    fun `chooses the shorter-distance path even when it has more hops than BFS would pick`() {
        val a = createWarehouse("A")
        val b = createWarehouse("B")
        val c = createWarehouse("C")
        val e = createWarehouse("E")
        val d = createWarehouse("D")

        // BFS would pick this: A -> B -> D (2 hops, but distance = 200)
        a.addRoute(createRoute("R1", a, b, distanceKm = 100.0))
        b.addRoute(createRoute("R2", b, d, distanceKm = 100.0))

        // Dijkstra should pick this instead: A -> C -> E -> D (3 hops, but distance = 30)
        a.addRoute(createRoute("R3", a, c, distanceKm = 10.0))
        c.addRoute(createRoute("R4", c, e, distanceKm = 10.0))
        e.addRoute(createRoute("R5", e, d, distanceKm = 10.0))

        val bfsPath = BreadthFirstRouter().findLeastHopPath(a, d)
        val dijkstraPath = DijkstraRouter().findShortestPath(a, d)

        assertEquals(listOf("A", "B", "D"), bfsPath.map { it.id })
        assertEquals(listOf("A", "C", "E", "D"), dijkstraPath.map { it.id })
    }

    @Test
    fun `returns empty path when destination is unreachable`() {
        val a = createWarehouse("A")
        val isolated = createWarehouse("Z")

        val path = DijkstraRouter().findShortestPath(a, isolated)

        assertEquals(emptyList<Warehouse>(), path)
    }

    @Test
    fun `does not loop forever when the graph contains a cycle`() {
        val a = createWarehouse("A")
        val b = createWarehouse("B")
        val c = createWarehouse("C")

        a.addRoute(createRoute("R1", a, b, distanceKm = 5.0))
        b.addRoute(createRoute("R2", b, a, distanceKm = 5.0)) // cycle back to A
        b.addRoute(createRoute("R3", b, c, distanceKm = 5.0))

        val path = DijkstraRouter().findShortestPath(a, c)

        assertEquals(listOf("A", "B", "C"), path.map { it.id })
    }

    private fun createWarehouse(id: String): Warehouse {
        return Warehouse(
            id = id,
            name = "Warehouse $id",
            regionalZone = "Test Zone",
            latitude = 0.0,
            longitude = 0.0
        )
    }

    private fun createRoute(
        id: String,
        origin: Warehouse,
        destination: Warehouse,
        distanceKm: Double
    ): Route {
        return Route(
            id = id,
            originHub = origin,
            destinationHub = destination,
            distanceKm = distanceKm,
            typicalDelayMin = 0.0
        )
    }
}
