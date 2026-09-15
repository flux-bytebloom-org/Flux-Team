package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseFleet
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository

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
            .mapNotNull { it.toDomain() }
            //.mapNotNull { it.toDomain(warehouseMap) }
            .onEach { it.currentHub.addVehicle(it) }
    }

    override fun getAll(): List<Vehicle> = vehicles

    override fun updateVehicleCurrentHub(vehicle: Vehicle, newHub: Warehouse): Vehicle {
        val oldHubId = vehicle.currentHub.id

        println("[[temp]] (1/3) Removing vehicle ${vehicle.id} from hub $oldHubId's fleet")
        println("[[temp]] (2/3) Updating vehicle ${vehicle.id}'s hub -> ${newHub.id}")
        println("[[temp]] (3/3) Registering vehicle ${vehicle.id} in hub ${newHub.id}'s fleet")

        val newVehicle = vehicle.copy(currentHub = newHub ) // TODO(delete)
        return newVehicle
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

