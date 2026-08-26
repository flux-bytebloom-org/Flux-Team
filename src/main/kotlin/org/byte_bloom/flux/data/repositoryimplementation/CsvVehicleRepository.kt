package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseFleet
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.repository.VehicleRepository

class CsvVehicleRepository(
    private val filePath: String
) : VehicleRepository {

    override fun getAll(): List<Vehicle> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parseFleet(cleanedLines).map { it.toDomain() }
    }
}
