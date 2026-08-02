package org.byte_bloom.flux.data.dataholders

import org.byte_bloom.flux.domain.operations.sorting.sortCargoByWeightDescending

class WarehouseRaw(
    val id: String,
    val name: String,
    val regionalZone: String,
    val latitude: Double,
    val longitude: Double
)
