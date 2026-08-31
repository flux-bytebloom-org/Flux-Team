package org.byte_bloom.flux.domain.response

import org.byte_bloom.flux.domain.model.Warehouse

data class WeightedPath(
    val path: List<Warehouse>,
    val packageCount: Int
)
