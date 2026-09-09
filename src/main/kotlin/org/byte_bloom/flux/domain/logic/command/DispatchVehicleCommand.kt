package org.byte_bloom.flux.domain.logic.command

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.usecase.DispatchVehicleUseCase
import org.byte_bloom.flux.domain.response.DispatchedVehicle

class DispatchVehicleCommand(
    private val hub: Warehouse,
    private val vehicle: Vehicle,
    private val dispatchVehicleUseCase: DispatchVehicleUseCase = DispatchVehicleUseCase()
) : Command {

    private lateinit var result: DispatchedVehicle

    override fun execute() {
        result = dispatchVehicleUseCase(hub, vehicle)
    }


    override fun undo() {
        if (!::result.isInitialized) return

        result.loadedPackages.forEach { hub.addPackage(it) }
        hub.sortCargoQueue()
    }

    override fun describe(): String =
        "DispatchVehicle[vehicle=${vehicle.id}, hub=${hub.id}, " +
                "packagesLoaded=${result.loadedPackages.size}, totalWeight=${result.totalWeight}]"
}
