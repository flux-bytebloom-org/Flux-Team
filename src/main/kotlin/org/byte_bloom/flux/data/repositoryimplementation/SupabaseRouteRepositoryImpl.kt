package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.remote.datasource.RemoteRouteDataSource
import org.byte_bloom.flux.data.remote.dto.toDomain
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class SupabaseRouteRepositoryImpl(
    private val remoteDataSource: RemoteRouteDataSource,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    override suspend fun getAll(): List<Route> {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return remoteDataSource.getAll()
            .mapNotNull { it.toDomain(warehousesById) }
            .onEach { route -> route.originHub.addRoute(route) }
    }
}
