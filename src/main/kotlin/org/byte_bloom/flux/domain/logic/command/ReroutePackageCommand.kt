package org.byte_bloom.flux.domain.logic.command

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.usecase.ReroutePackageUseCase

class ReroutePackageCommand(
    private val originHub: Warehouse,
    private val packageItem: Package,
    private val newDestination: Warehouse,
    private val reroutePackageUseCase: ReroutePackageUseCase
) : Command {

    private val originalDestination: Warehouse = packageItem.destinationHub
    private var reroutedPackage: Package? = null

    override fun execute() {
        reroutedPackage = reroutePackageUseCase(originHub, packageItem, newDestination)
    }

    override fun undo() {
        val rerouted = reroutedPackage ?: return
        originHub.removePackage(rerouted)

        val restoredPackage = rerouted.copy(destinationHub = originalDestination)
        originHub.addPackage(restoredPackage)
        originHub.sortCargoQueue()

        reroutedPackage = null
    }
}
