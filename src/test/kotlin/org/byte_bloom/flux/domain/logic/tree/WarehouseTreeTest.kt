package org.byte_bloom.flux.domain.logic.tree

import org.byte_bloom.flux.domain.model.Warehouse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WarehouseTreeTest {

    @Test
    fun `should trace local depot lineage to global hub`() {

        val global = WarehouseTreeNode(
            Warehouse(
                "G1",
                "Global Hub",
                "Global",
                0.0,
                0.0
            )
        )

        val regional = WarehouseTreeNode(
            Warehouse(
                "R1",
                "Regional Center",
                "North",
                0.0,
                0.0
            )
        )

        val local = WarehouseTreeNode(
            Warehouse(
                "L1",
                "Local Depot",
                "North",
                0.0,
                0.0
            )
        )

        global.addChild(regional)
        regional.addChild(local)

        val tree = WarehouseTree(global)

        val lineage = tree.findLineage("L1")

        assertEquals(
            listOf("L1", "R1", "G1"),
            lineage.map { it.warehouse.id }
        )
    }
}

@Test
fun `should trace lineage through multiple regional centers`() {

    val global = WarehouseTreeNode(
        Warehouse("G1", "Global Hub", "Global", 0.0, 0.0)
    )

    val regionalNorth = WarehouseTreeNode(
        Warehouse("R1", "North Regional", "North", 0.0, 0.0)
    )

    val regionalSouth = WarehouseTreeNode(
        Warehouse("R2", "South Regional", "South", 0.0, 0.0)
    )

    val localNorth = WarehouseTreeNode(
        Warehouse("L1", "North Local", "North", 0.0, 0.0)
    )

    val localSouth = WarehouseTreeNode(
        Warehouse("L2", "South Local", "South", 0.0, 0.0)
    )

    global.addChild(regionalNorth)
    global.addChild(regionalSouth)

    regionalNorth.addChild(localNorth)
    regionalSouth.addChild(localSouth)

    val tree = WarehouseTree(global)

    val lineage = tree.findLineage("L2")

    assertEquals(
        listOf("L2", "R2", "G1"),
        lineage.map { it.warehouse.id }
    )
}