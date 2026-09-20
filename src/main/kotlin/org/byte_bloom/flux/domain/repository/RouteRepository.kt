package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Route

interface RouteRepository {
    suspend fun getAll(): List<Route>

    suspend fun getById(id: String): Route?
    suspend fun create(route: Route): Route
    suspend fun update(id: String, warehouse: Route): Route
    suspend fun delete(id: String)
}

