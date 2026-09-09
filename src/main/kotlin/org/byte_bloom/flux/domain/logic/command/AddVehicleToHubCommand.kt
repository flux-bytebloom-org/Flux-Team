package org.byte_bloom.flux.domain.logic.command

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.usecase.AddVehicleToHubUseCase

class AddVehicleToHubCommand(
    private val hub: Warehouse,
    private val vehicle: Vehicle,
    private val addVehicleToHubUseCase: AddVehicleToHubUseCase
) : Command {

    private lateinit var previousHub: Warehouse
    private lateinit var addedVehicle: Vehicle

    override fun execute() {
        previousHub = vehicle.currentHub
        previousHub.removeVehicle(vehicle)
        addedVehicle = addVehicleToHubUseCase(hub, vehicle)
    }

    override fun undo() {
        if (!::addedVehicle.isInitialized) return

        hub.removeVehicle(addedVehicle)
        val restoredVehicle = addedVehicle.copy(currentHub = previousHub)
        previousHub.addVehicle(restoredVehicle)
    }

    override fun describe(): String =
        "AddVehicleToHub[vehicle=${vehicle.id}, from=${previousHub.id}, to=${hub.id}, hubFleetSizeNow=${hub.getStationedVehicles().size}]"
}
