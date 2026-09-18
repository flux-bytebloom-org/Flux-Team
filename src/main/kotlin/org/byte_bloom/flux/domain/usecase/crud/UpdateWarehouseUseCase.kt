package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class UpdateWarehouseUseCase(private val repository: WarehouseRepository) {
    suspend operator fun invoke(id: String, warehouse: Warehouse): Warehouse = repository.update(id, warehouse)
}