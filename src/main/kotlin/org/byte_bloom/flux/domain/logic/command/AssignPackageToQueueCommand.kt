package org.byte_bloom.flux.domain.logic.command

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.usecase.AssignPackageToCargoQueueUseCase

class AssignPackageToQueueCommand(
    private val hub: Warehouse,
    private val packageItem: Package,
    private val assignPackageToCargoQueueUseCase: AssignPackageToCargoQueueUseCase
) : Command {

    override fun execute() {
        assignPackageToCargoQueueUseCase(hub, packageItem)
    }

    override fun undo() {
        hub.removePackage(packageItem)
    }
}
