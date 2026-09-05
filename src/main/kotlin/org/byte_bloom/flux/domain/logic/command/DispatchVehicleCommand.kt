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

    private var queueBeforeDispatch: List<Package> = emptyList()
    private var dispatchedPackages: List<Package> = emptyList()

    override fun execute() {
        queueBeforeDispatch = hub.getCargoQueue()
        dispatchedPackages = dispatchVehicleUseCase(hub, vehicle)
        dispatchedPackages.forEach(hub::removePackage)
        println("  hub=${hub.id} vehicle=${vehicle.id} loaded=${dispatchedPackages.map { it.id }} remainingQueue=${hub.getCargoQueue().map { it.id }}")
    }


    override fun undo() {
        hub.getCargoQueue().forEach(hub::removePackage)
        queueBeforeDispatch.forEach(hub::addPackage)
        println("  hub=${hub.id} vehicle=${vehicle.id} unloaded=${dispatchedPackages.map { it.id }} restoredQueue=${hub.getCargoQueue().map { it.id }}")
    }
        override fun toString(): String =
            "DispatchVehicle(vehicle=${vehicle.id}, hub=${hub.id}, packages=${dispatchedPackages.map { it.id }})"
    }

