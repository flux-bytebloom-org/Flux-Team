package org.byte_bloom.flux.domain.logic.tree

import org.byte_bloom.flux.domain.model.Warehouse

private const val REGIONAL_DEPOT_COUNT = 1

fun buildWarehouseTree(warehouses: List<Warehouse>, globalHub: Warehouse): WarehouseTree {

    val root = WarehouseTreeNode(globalHub)

    val warehousesByZone = warehouses
        .filter { it.id != globalHub.id }
        .groupBy { it.regionalZone }

    warehousesByZone.forEach { (zone, warehousesInZone) ->
        val regionalWarehouse = warehousesInZone.first().copy(name = "$zone Regional Depot")
        val regionalDepotNode = WarehouseTreeNode(regionalWarehouse)
        root.addChild(regionalDepotNode)

        warehousesInZone.drop(REGIONAL_DEPOT_COUNT).forEach { warehouse ->
            regionalDepotNode.addChild(WarehouseTreeNode(warehouse))
        }
    }

    return WarehouseTree(root)
}
