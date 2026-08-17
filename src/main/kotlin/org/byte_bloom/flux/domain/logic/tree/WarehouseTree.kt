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

    fun findNode(node: WarehouseTreeNode, targetId: String): WarehouseTreeNode? {
        if (node.warehouse.id == targetId) return node

        return node.children.firstNotNullOfOrNull { child ->
            findNode(child, targetId)
        }
    }
}
