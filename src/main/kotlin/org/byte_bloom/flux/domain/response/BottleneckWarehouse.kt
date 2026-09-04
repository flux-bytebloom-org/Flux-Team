package org.byte_bloom.flux.domain.response

import org.byte_bloom.flux.domain.model.Warehouse

data class BottleneckWarehouse(
    val warehouse: Warehouse,
    val transitLoad: Int
)
