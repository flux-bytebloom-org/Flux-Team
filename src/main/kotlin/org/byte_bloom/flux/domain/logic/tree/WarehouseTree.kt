package org.byte_bloom.flux.domain.logic.tree

class WarehouseTree(
    val root: WarehouseTreeNode
) {
    fun findNode(hubId: String, currentNode: WarehouseTreeNode = root): WarehouseTreeNode? {
        if (currentNode.warehouse.id == hubId) {
            return currentNode
        }

        return currentNode.children.firstNotNullOfOrNull { child ->
            findNode(hubId, child)
        }
    }
}
