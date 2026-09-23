package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.csv.datasource.RouteDataSource as LocalDataSourse
import org.byte_bloom.flux.data.csv.mapper.toDomain
import org.byte_bloom.flux.data.remote.datasource.RemoteRouteDataSource
import org.byte_bloom.flux.data.remote.dto.toDomain
import org.byte_bloom.flux.data.remote.dto.toRequestDto
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class RouteRepositoryImpl(
    private val localRouteDataSource: LocalDataSourse,
    private val remoteRouteDataSource: RemoteRouteDataSource,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    override suspend fun getAll(): List<Route> {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return localRouteDataSource.getAll()
            .mapNotNull { it.toDomain(warehousesById) }
            .onEach { route -> route.originHub.addRoute(route) }
    }

    override suspend fun getById(id: String): Result<Route> = runCatching {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        remoteRouteDataSource.getById(id)?.toDomain(warehousesById)
            ?: throw LogisticsException.EntityNotFoundException.RouteNotFoundException(id)
    }

    override suspend fun create(route: Route): Result<Route> = runCatching {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        remoteRouteDataSource.create(route.toRequestDto()).toDomain(warehousesById)
            ?: throw LogisticsException.EntityNotFoundException.WarehouseNotFoundException(route.originHub.id)
    }

    override suspend fun update(id: String, route: Route): Result<Route> = runCatching {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        remoteRouteDataSource.update(id, route.toRequestDto()).toDomain(warehousesById)
            ?: throw LogisticsException.EntityNotFoundException.WarehouseNotFoundException(route.originHub.id)
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        remoteRouteDataSource.delete(id)
    }
}
