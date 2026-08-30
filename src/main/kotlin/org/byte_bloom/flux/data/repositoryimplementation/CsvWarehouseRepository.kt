package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseWarehouses
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository

class CsvWarehouseRepository(
    private val filePath: String
) : WarehouseRepository {

    override fun getAll(): List<Warehouse> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parseWarehouses(cleanedLines).map { it.toDomain() }
    }
}

