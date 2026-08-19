package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseRoutes
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository

class CsvRouteRepository(
    private val filePath: String
) : RouteRepository {

    override fun getAll(): List<Route> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parseRoutes(cleanedLines).map { it.toDomain() }
    }
}
