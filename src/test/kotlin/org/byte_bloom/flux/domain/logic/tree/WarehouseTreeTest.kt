package org.byte_bloom.flux.domain.logic.tree

import org.byte_bloom.flux.domain.model.Warehouse

class WarehouseTreeTest {

    fun shouldTraceLocalDepotLineageToGlobalHub() {
        val globalNode = createWarehouseNode(GLOBAL_ID, "Global Hub", ZONE_GLOBAL)
        val regionalNode = createWarehouseNode(REGIONAL_NORTH_ID, "Regional Center", ZONE_NORTH)
        val localNode = createWarehouseNode(LOCAL_NORTH_ID, "Local Depot", ZONE_NORTH)

        globalNode.addChild(regionalNode)
        regionalNode.addChild(localNode)

        val tree = WarehouseTree(globalNode)
        val lineage = tree.findLineage(LOCAL_NORTH_ID)

        val expectedIds = listOf(LOCAL_NORTH_ID, REGIONAL_NORTH_ID, GLOBAL_ID)
        val actualIds = lineage.map { it.warehouse.id }

        assertThat(expectedIds == actualIds, "Local depot lineage mismatch")
    }

    fun shouldTraceLineageThroughMultipleRegionalCenters() {
        val globalNode = createWarehouseNode(GLOBAL_ID, "Global Hub", ZONE_GLOBAL)
        val regionalNorth = createWarehouseNode(REGIONAL_NORTH_ID, "North Regional", ZONE_NORTH)
        val regionalSouth = createWarehouseNode(REGIONAL_SOUTH_ID, "South Regional", ZONE_SOUTH)
        val localSouth = createWarehouseNode(LOCAL_SOUTH_ID, "South Local", ZONE_SOUTH)

        globalNode.addChild(regionalNorth)
        globalNode.addChild(regionalSouth)
        regionalSouth.addChild(localSouth)

        val tree = WarehouseTree(globalNode)
        val lineage = tree.findLineage(LOCAL_SOUTH_ID)

        val expectedIds = listOf(LOCAL_SOUTH_ID, REGIONAL_SOUTH_ID, GLOBAL_ID)
        val actualIds = lineage.map { it.warehouse.id }

        assertThat(expectedIds == actualIds, "Multi-regional lineage mismatch")
    }

    private fun createWarehouseNode(id: String, name: String, zone: String): WarehouseTreeNode {
        return WarehouseTreeNode(
            Warehouse(
                id = id,
                name = name,
                regionalZone = zone,
                latitude = DEFAULT_COORDINATE,
                longitude = DEFAULT_COORDINATE
            )
        )
    }

    private fun assertThat(condition: Boolean, message: String) {
        if (!condition) {
            throw AssertionError("Test Failed: $message")
        }
    }

    companion object {
        private const val GLOBAL_ID = "G1"
        private const val REGIONAL_NORTH_ID = "R1"
        private const val REGIONAL_SOUTH_ID = "R2"
        private const val LOCAL_NORTH_ID = "L1"
        private const val LOCAL_SOUTH_ID = "L2"
        private const val ZONE_GLOBAL = "Global"
        private const val ZONE_NORTH = "North"
        private const val ZONE_SOUTH = "South"
        private const val DEFAULT_COORDINATE = 0.0
    }
}

fun main() {
    val testRunner = WarehouseTreeTest()
    println("Running WarehouseTreeTest...")

    testRunner.shouldTraceLocalDepotLineageToGlobalHub()
    testRunner.shouldTraceLineageThroughMultipleRegionalCenters()

    println("All WarehouseTree tests passed successfully!")
}
