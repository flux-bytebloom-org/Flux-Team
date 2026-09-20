package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository

class GetRouteByIdUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(id: String): Route? = repository.getById(id)
}
