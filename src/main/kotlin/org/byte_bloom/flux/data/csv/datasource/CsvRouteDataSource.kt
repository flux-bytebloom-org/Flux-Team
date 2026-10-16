package org.byte_bloom.flux.data.csv.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.byte_bloom.flux.data.csv.dataholders.RouteRaw
import org.byte_bloom.flux.data.csv.parsers.cleanLines
import org.byte_bloom.flux.data.csv.parsers.parseRoutes
import org.byte_bloom.flux.data.csv.readers.readCsv

class CsvRouteDataSource (
    private val filePath: String
) : RouteDataSource {
    override suspend fun getAll(): List<RouteRaw> = withContext(Dispatchers.IO) {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        parseRoutes(cleanedLines)
    }
}
