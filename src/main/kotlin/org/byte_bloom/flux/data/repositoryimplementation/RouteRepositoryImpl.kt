package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.datasource.RouteDataSource
import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseRoutes
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository


class RouteRepositoryImpl(
    private val routeDataSource: RouteDataSource,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    override fun getAll(): List<Route> {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return routeDataSource.getAll()
            .mapNotNull { it.toDomain(warehousesById) }
            .onEach { route -> route.originHub.addRoute(route) }
    }
}

