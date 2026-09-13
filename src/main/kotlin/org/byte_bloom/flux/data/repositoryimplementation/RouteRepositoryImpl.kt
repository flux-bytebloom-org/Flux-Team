package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseRoutes
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository

class RouteRepositoryImpl(
    private val routeDataSource: RouteDataSource
) : RouteRepository {

    override fun getAll(): List<Route> =
        routeDataSource.getAll().map { it.toDomain() }

}

