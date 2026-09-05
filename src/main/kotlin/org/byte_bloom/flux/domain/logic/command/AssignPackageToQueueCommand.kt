package org.byte_bloom.flux.domain.logic.command

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse

class AssignPackageToQueueCommand(
    private val hub: Warehouse,
    private val packageItem: Package
) : Command {

    override fun execute() {
        hub.addPackage(packageItem)
        println("  hub=${hub.id} added=${packageItem.id} queue=${hub.getCargoQueue().map { it.id }}")
    }

    override fun undo() {
        hub.removePackage(packageItem)
        println("  hub=${hub.id} removed=${packageItem.id} queue=${hub.getCargoQueue().map { it.id }}")
    }

    override fun toString(): String = "AssignPackageToQueue(package=${packageItem.id}, hub=${hub.id})"
}
