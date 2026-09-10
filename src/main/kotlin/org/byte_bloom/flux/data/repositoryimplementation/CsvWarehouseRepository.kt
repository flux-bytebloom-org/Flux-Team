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

    private val warehouses: List<Warehouse> by lazy {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)

        parseWarehouses(cleanedLines)
            .map { it.toDomain() }
    }

    override fun getAll(): List<Warehouse> = warehouses

    override fun updateWarehouseFleetListWithCurrentVehicle(hubId: String, vehicleId: String) {
        // Implementation for updating warehouse's fleet list with current vehicle
        println("[[temp]] Updating warehouse $hubId with vehicle $vehicleId in CSV repository (not implemented YET)")
    }
}

