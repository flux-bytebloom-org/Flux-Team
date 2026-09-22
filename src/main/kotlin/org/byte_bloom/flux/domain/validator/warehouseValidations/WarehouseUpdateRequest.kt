package org.byte_bloom.flux.domain.validator.warehouseValidations

data class WarehouseUpdateRequest(
    val name: String?,
    val regionalZone: String?,
    val latitude: Double?,
    val longitude: Double?
)