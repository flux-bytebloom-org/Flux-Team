package org.byte_bloom.flux.domain.logic.command

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.usecase.DispatchVehicleUseCase

class DispatchVehicleCommand(
    private val hub: Warehouse,
    private val vehicle: Vehicle,
    private val dispatchVehicleUseCase: DispatchVehicleUseCase = DispatchVehicleUseCase()
) : Command {

    //private val dispatchedPackages = mutableListOf<Package>()
    private var queueBeforeDispatch: List<Package> = emptyList()

    override fun execute() {
        queueBeforeDispatch = hub.getCargoQueue()
        val loadedPackages = dispatchVehicleUseCase(hub, vehicle)
        loadedPackages.forEach(hub::removePackage)
    }


    override fun undo() {
        hub.getCargoQueue().forEach(hub::removePackage)
        queueBeforeDispatch.forEach(hub::addPackage)
    }
}
