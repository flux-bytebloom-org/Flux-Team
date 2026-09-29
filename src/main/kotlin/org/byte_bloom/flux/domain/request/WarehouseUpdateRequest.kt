package org.byte_bloom.flux.domain.request

data class WarehouseUpdateRequest(
    val name: String?,
    val regionalZone: String?,
    val latitude: Double?,
    val longitude: Double?
)