package org.byte_bloom.flux.domain.logic.tree

import org.byte_bloom.flux.domain.model.Warehouse

fun buildWarehouseTree(warehouses: List<Warehouse>, globalHub: Warehouse): WarehouseTree {

    val root = WarehouseTreeNode(globalHub)

    val warehousesByZone = warehouses
        .filter { it.id != globalHub.id }
        .groupBy { it.regionalZone }

    warehousesByZone.forEach { (zone, warehousesInZone) ->
        val regionalDepotNode = WarehouseTreeNode(
            warehousesInZone.first().copy(name = "$zone Regional Depot")
        )
        root.addChild(regionalDepotNode)

        warehousesInZone.forEach { warehouse ->
            regionalDepotNode.addChild(WarehouseTreeNode(warehouse))
        }
    }

    return WarehouseTree(root)
}
