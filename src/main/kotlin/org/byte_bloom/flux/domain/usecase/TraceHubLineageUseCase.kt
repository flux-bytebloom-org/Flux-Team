package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.logic.tree.WarehouseTreeNode
import org.byte_bloom.flux.domain.model.Warehouse

class TraceHubLineageUseCase {

    operator fun invoke(leafNode: WarehouseTreeNode): List<Warehouse> {

        return generateSequence(leafNode) { it.parent }
            .map { it.warehouse }
            .toList()
    }
}

