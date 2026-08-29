package org.byte_bloom.flux.domain.logic.routing

import org.byte_bloom.flux.domain.model.Warehouse

data class RoutingSearchResult(
    val path: List<Warehouse>,
    val nodesExplored: Int
)

