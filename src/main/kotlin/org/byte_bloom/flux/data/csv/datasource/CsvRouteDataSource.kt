package org.byte_bloom.flux.data.csv.datasource

import org.byte_bloom.flux.data.csv.dataholders.RouteRaw
import org.byte_bloom.flux.data.csv.parsers.cleanLines
import org.byte_bloom.flux.data.csv.parsers.parseRoutes
import org.byte_bloom.flux.data.csv.readers.readCsv

class CsvRouteDataSource (
    private val filePath: String
) : RouteDataSource {
    override fun getAll(): List<RouteRaw> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parseRoutes(cleanedLines)
    }
}