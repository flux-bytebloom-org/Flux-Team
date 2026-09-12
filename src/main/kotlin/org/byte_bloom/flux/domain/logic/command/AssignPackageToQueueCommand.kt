package org.byte_bloom.flux.domain.logic.command

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.usecase.AssignPackageToCargoQueueUseCase
import org.byte_bloom.flux.domain.usecase.RemovePackageFromQueueUseCase

class AssignPackageToQueueCommand(
    private val hub: Warehouse,
    private val packageItem: Package,
    private val assignPackageToCargoQueueUseCase: AssignPackageToCargoQueueUseCase,
    private val removePackageFromQueueUseCase: RemovePackageFromQueueUseCase
) : Command {

    override fun execute() {
        assignPackageToCargoQueueUseCase(hub, packageItem)
    }

    override fun undo() {
        removePackageFromQueueUseCase(hub,packageItem)

    }

    override fun describe(): String =
        "AssignPackageToQueue[pkg=${packageItem.id}, hub=${hub.id}, queueSizeNow=${hub.getCargoQueue().size}]"
}
