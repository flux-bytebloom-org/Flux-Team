package org.byte_bloom.flux.data.csv.datasource

import org.byte_bloom.flux.data.csv.dataholders.WarehouseRaw
import org.byte_bloom.flux.data.csv.parsers.cleanLines
import org.byte_bloom.flux.data.csv.parsers.parseWarehouses
import org.byte_bloom.flux.data.csv.readers.readCsv

class CsvWarehouseDataSource(
    private val filePath: String
) : WarehouseDataSource {
    override fun getAll(): List<WarehouseRaw> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parseWarehouses(cleanedLines)
    }
}
