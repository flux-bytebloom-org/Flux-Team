package org.byte_bloom.flux.domain.logic.command

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse

class AssignPackageToQueueCommand(
    private val hub: Warehouse,
    private val packageItem: Package
) : Command {

    override fun execute() {
        hub.addPackage(packageItem)
    }

    override fun undo() {
        hub.removePackage(packageItem)
    }
}
