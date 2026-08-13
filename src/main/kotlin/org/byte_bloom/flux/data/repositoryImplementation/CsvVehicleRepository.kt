package org.byte_bloom.flux.data.repositoryImplementation
import org.byte_bloom.flux.data.dataholders.VehicleRaw
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseFleet
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.repository.VehicleRepository

class CsvVehicleRepository(
    private val filePath: String
) : VehicleRepository {

    override fun getAll(): List<VehicleRaw> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parseFleet(cleanedLines)
    }
}