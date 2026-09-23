package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Route

interface RouteRepository {
    suspend fun getAll(): List<Route>
    suspend fun getById(id: String): Result<Route>
    suspend fun create(route: Route): Result<Route>
    suspend fun update(id: String, route: Route): Result<Route>
    suspend fun delete(id: String): Result<Unit>
}
