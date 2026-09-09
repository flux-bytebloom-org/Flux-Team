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

    private lateinit var originalDestination: Warehouse
    private lateinit var reroutedPackage: Package

    override fun execute() {
        originalDestination = packageItem.destinationHub
        reroutedPackage = reroutePackageUseCase(originHub, packageItem, newDestination)
    }

    override fun undo() {
        if (!::reroutedPackage.isInitialized) return
        originHub.removePackage(reroutedPackage)

        val restoredPackage = reroutedPackage.copy(destinationHub = originalDestination)
        originHub.addPackage(restoredPackage)
        originHub.sortCargoQueue()
    }

    override fun describe(): String =
        "ReroutePackage[pkg=${packageItem.id}, from=${originalDestination.id}, to=${newDestination.id}, hub=${originHub.id}]"
}
