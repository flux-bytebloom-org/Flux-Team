package org.byte_bloom.flux.data.remote.mapper

import org.byte_bloom.flux.data.remote.dto.warehouse.WarehouseResponseDto
import org.byte_bloom.flux.domain.model.Warehouse

class WarehouseDtoMapper {
    fun toDomain(dto: WarehouseResponseDto): Warehouse = Warehouse(
        id = dto.id,
        name = dto.name,
        regionalZone = dto.regionalZone,
        latitude = dto.latitude,
        longitude = dto.longitude
    )
}