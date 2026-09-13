package org.byte_bloom.flux.data.datasource.csv

import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.data.datasource.RouteDataSource
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseRoutes
import org.byte_bloom.flux.data.readers.readCsv

class CsvRouteDataSource (
    private val filePath: String
) : RouteDataSource {
    override fun getAll(): List<RouteRaw> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parseRoutes(cleanedLines)
    }
}
