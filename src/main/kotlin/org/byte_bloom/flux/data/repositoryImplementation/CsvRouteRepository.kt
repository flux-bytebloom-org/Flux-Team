package org.byte_bloom.flux.data.repositoryImplementation

import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseRoutes
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.repository.RouteRepository

class CsvRouteRepository(
    private val filePath: String
) : RouteRepository {

    override fun getAll(): List<RouteRaw> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parseRoutes(cleanedLines)
    }
}