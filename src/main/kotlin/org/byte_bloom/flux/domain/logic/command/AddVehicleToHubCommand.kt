package org.byte_bloom.flux.domain.logic.command

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.usecase.AddVehicleToHubUseCase

class AddVehicleToHubCommand(
    private val hub: Warehouse,
    private val vehicle: Vehicle,
    private val addVehicleToHubUseCase: AddVehicleToHubUseCase
) : Command {

    private val previousHub: Warehouse? = vehicle.currentHub
    private var addedVehicle: Vehicle? = null

    override fun execute() {
        val updated = vehicle.copy(currentHub = hub)
        addedVehicle = updated
        addVehicleToHubUseCase(hub, vehicle)
    }

    override fun undo() {
        val currentAdded = addedVehicle ?: return
        hub.removeVehicle(currentAdded)
        previousHub?.let { prev ->
            val restoredVehicle = currentAdded.copy(currentHub = prev)
            prev.addVehicle(restoredVehicle)
        }

        addedVehicle = null
    }
}
