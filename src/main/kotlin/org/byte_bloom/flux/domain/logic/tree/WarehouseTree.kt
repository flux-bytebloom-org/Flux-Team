package org.byte_bloom.flux.domain.logic.tree

class WarehouseTree(
    val root: WarehouseTreeNode
) {

    fun findLineage(
        warehouseId: String
    ): List<WarehouseTreeNode> {

        val node = findNode(root, warehouseId)
            ?: return emptyList()

        val lineage = mutableListOf<WarehouseTreeNode>()

        var current: WarehouseTreeNode? = node

        while (current != null) {
            lineage.add(current)
            current = current.parent
        }

        return lineage
    }

    private fun findNode(
        current: WarehouseTreeNode,
        warehouseId: String
    ): WarehouseTreeNode? {

        if (current.warehouse.id == warehouseId) {
            return current
        }

        for (child in current.getChildren()) {

            val result = findNode(
                child,
                warehouseId
            )

            if (result != null) {
                return result
            }
        }

        return null
    }
}