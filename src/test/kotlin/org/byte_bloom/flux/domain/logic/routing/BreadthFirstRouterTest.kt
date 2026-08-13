package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Warehouse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BreadthFirstRouterTest {

    @Test
    fun `should find path with least transfer hops`() {

        val warehouseA = createWarehouse("A")
        val warehouseB = createWarehouse("B")
        val warehouseC = createWarehouse("C")
        val warehouseD = createWarehouse("D")

        warehouseA.addRoute(
            createRoute("R1", warehouseA, warehouseB)
        )

        warehouseA.addRoute(
            createRoute("R2", warehouseA, warehouseC)
        )

        warehouseB.addRoute(
            createRoute("R3", warehouseB, warehouseD)
        )

        warehouseC.addRoute(
            createRoute("R4", warehouseC, warehouseD)
        )

        val router = BreadthFirstRouter()

        val path = router.findLeastHopPath(
            start = warehouseA,
            destination = warehouseD
        )

        assertEquals(
            listOf("A", "B", "D"),
            path.map { it.id }
        )
    }

    @Test
    fun `should return empty path when destination is unreachable`() {

        val warehouseA = createWarehouse("A")
        val warehouseB = createWarehouse("B")
        val warehouseC = createWarehouse("C")

        warehouseA.addRoute(
            createRoute("R1", warehouseA, warehouseB)
        )

        val router = BreadthFirstRouter()

        val path = router.findLeastHopPath(
            start = warehouseA,
            destination = warehouseC
        )

        assertEquals(emptyList<Warehouse>(), path)
    }

    @Test
    fun `should return same warehouse when start equals destination`() {

        val warehouse = createWarehouse("A")

        val router = BreadthFirstRouter()

        val path = router.findLeastHopPath(
            start = warehouse,
            destination = warehouse
        )

        assertEquals(
            listOf("A"),
            path.map { it.id }
        )
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
        destination: Warehouse
    ): Route {
        return Route(
            id = id,
            originHub = origin,
            destinationHub = destination,
            distanceKm = 10.0,
            typicalDelayMin = 5.0
        )
    }
}