package org.byte_bloom.flux.data.csv.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.byte_bloom.flux.data.csv.dataholders.WarehouseRaw
import org.byte_bloom.flux.data.csv.parsers.cleanLines
import org.byte_bloom.flux.data.csv.parsers.parseWarehouses
import org.byte_bloom.flux.data.csv.readers.readCsv

class CsvWarehouseDataSource(
    private val filePath: String
) : WarehouseDataSource {
    override suspend fun getAll(): List<WarehouseRaw> = withContext(Dispatchers.IO) {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        parseWarehouses(cleanedLines)
    }
}
