package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository

class CreateRouteUseCase(
    private val repository: RouteRepository
){
    suspend operator fun invoke(route : Route): Route{
        return repository.create(route)
    }
}
