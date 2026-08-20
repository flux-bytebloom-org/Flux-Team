package org.byte_bloom.flux.domain.logic.tree

import org.byte_bloom.flux.domain.model.Warehouse

class WarehouseTreeNode(
    val warehouse: Warehouse
) {

    var parent: WarehouseTreeNode? = null
        private set

    private val children = mutableListOf<WarehouseTreeNode>()

    fun addChild(child: WarehouseTreeNode) {
        child.parent = this
        children.add(child)
    }

    fun getChildren(): List<WarehouseTreeNode> {
        return children.toList()
    }
}
