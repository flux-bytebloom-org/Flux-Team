package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Route

interface RouteRepository {
    suspend fun getAll(): List<Route>
}

