package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseRoutes
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class CsvRouteRepository(
    private val filePath: String,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    private val routes: List<Route> by lazy {
        val warehouseMap = warehouseRepository
            .getAll()
            .associateBy { it.id }

        parseRoutes(
            cleanLines(readCsv(filePath))
        )
            .mapNotNull { it.toDomain(warehouseMap) }
            .onEach { it.originHub.addRoute(it) }
    }

    override fun getAll(): List<Route> = routes
}

