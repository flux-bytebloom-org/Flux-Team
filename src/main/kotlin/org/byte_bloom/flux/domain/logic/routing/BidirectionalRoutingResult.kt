package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Warehouse

data class BidirectionalRoutingResult(
    val path: List<Warehouse>,
    val nodesExplored: Int
)
