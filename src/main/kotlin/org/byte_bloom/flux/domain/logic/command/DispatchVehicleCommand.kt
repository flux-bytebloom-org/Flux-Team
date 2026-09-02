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

    private val dispatchedPackages = mutableListOf<Package>()

    override fun execute() {
        val loadedPackages = dispatchVehicleUseCase(hub, vehicle)
        dispatchedPackages.clear()
        dispatchedPackages.addAll(loadedPackages)

        loadedPackages.forEach { pkg ->
            hub.removePackage(pkg)
        }
    }

    override fun undo() {
        dispatchedPackages.forEach { pkg ->
            hub.addPackage(pkg)
        }
        dispatchedPackages.clear()
    }
}
