package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.domain.repository.WarehouseRepository

class DeleteWarehouseUseCase(private val repository: WarehouseRepository) {
    suspend operator fun invoke(id: String) = repository.delete(id)
}
