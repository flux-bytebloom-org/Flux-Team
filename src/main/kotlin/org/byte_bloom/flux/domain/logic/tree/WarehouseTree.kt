package org.byte_bloom.flux.domain.logic.tree

class WarehouseTree(
    val root: WarehouseTreeNode
) {
    fun findNode(hubId: String): WarehouseTreeNode? {
        return searchRecursive(hubId, root)
    }

    private fun searchRecursive(hubId: String, currentNode: WarehouseTreeNode): WarehouseTreeNode? {
        if (currentNode.warehouse.id == hubId) {
            return currentNode
        }
        return currentNode.children.firstNotNullOfOrNull { child ->
            searchRecursive(hubId, child)
        }
    }
}
