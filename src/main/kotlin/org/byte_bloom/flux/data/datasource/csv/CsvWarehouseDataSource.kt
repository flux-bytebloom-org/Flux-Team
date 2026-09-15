package org.byte_bloom.flux.data.datasource.csv

import org.byte_bloom.flux.data.dataholders.WarehouseRaw
import org.byte_bloom.flux.data.datasource.WarehouseDataSource
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseWarehouses
import org.byte_bloom.flux.data.readers.readCsv

class CsvWarehouseDataSource(
    private val filePath: String
) : WarehouseDataSource {
    override fun getAll(): List<WarehouseRaw> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parseWarehouses(cleanedLines)
    }
}
