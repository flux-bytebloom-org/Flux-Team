package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseFleet
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.repository.VehicleRepository

class CsvVehicleRepository(
    private val filePath: String,
    private val warehouseRepository: WarehouseRepository
) : VehicleRepository {

    private val vehicles: List<Vehicle> by lazy {
        val warehouseMap = warehouseRepository
            .getAll()
            .associateBy { it.id }

        parseFleet(
            cleanLines(readCsv(filePath))
        )
            .mapNotNull { it.toDomain(warehouseMap) }
            .onEach { it.currentHub.addVehicle(it) }
    }

    override fun getAll(): List<Vehicle> = vehicles

    override fun updateVehicleCurrentHub(vehicleId: String, hubId: String): Vehicle {
        println("[[temp]] (1/2) Updating vehicle $vehicleId's hub -> $hubId")
        println("[[temp]] (2/2) Registering vehicle $vehicleId in hub $hubId's fleet")

        val vehicle = getVehicleById(vehicleId)
        return vehicle
    }

    private fun getVehicleById(vehicleId: String): Vehicle {
        val vehicles = getAll()
        val unknownWarehouse = Warehouse(
            id = "xx",
            name = "Unknown",
            regionalZone = "Unknown",
            latitude = 0.0,
            longitude = 0.0
        )
        return vehicles.find { it.id == vehicleId }
            ?: return Vehicle(
                id = vehicleId,
                currentHub = unknownWarehouse,
                maxCapacityKg = 0.0,
                costPerKm = 0.0
            )
    }
}

