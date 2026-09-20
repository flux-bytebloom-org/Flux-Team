package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.domain.repository.RouteRepository

class DeleteRouteUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(id: String) = repository.delete(id)
}
