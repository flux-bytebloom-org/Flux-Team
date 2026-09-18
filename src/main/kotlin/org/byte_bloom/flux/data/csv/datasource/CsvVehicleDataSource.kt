package org.byte_bloom.flux.data.csv.datasource

import org.byte_bloom.flux.data.csv.dataholders.VehicleRaw
import org.byte_bloom.flux.data.csv.parsers.cleanLines
import org.byte_bloom.flux.data.csv.parsers.parseFleet
import org.byte_bloom.flux.data.csv.readers.readCsv

class CsvVehicleDataSource(
    private val filePath: String
) : VehicleDataSource {

    override fun getAll(): List<VehicleRaw> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parseFleet(cleanedLines)
    }

    override fun updateCurrentHub(vehicleId: String, oldHubId: String, newHubId: String) {
        println("[[temp]] (1/3) Removing vehicle $vehicleId from hub $oldHubId's fleet")
        println("[[temp]] (2/3) Updating vehicle $vehicleId's hub -> $newHubId")
        println("[[temp]] (3/3) Registering vehicle $vehicleId in hub $newHubId's fleet")

        // LATER: persist vehicle's currentHubId change in the fleet CSV file (this data source's own file)
        // once WarehouseDataSource exposes fleet-management methods:
        // warehouseDataSource.removeVehicleFromFleet(oldHubId, vehicleId)
        // warehouseDataSource.addVehicleToFleet(newHubId, vehicleId)

    }


}
