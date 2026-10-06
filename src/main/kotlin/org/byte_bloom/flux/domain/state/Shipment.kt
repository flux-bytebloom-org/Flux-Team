package org.byte_bloom.flux.domain.state

import org.byte_bloom.flux.domain.model.Package

class Shipment(val pkg: Package) {

    var state: ShipmentState = CreatedState
        private set

    private val log = mutableListOf<String>()
    val history: List<String> get() = log.toList()

    fun assignToVehicle() = moveTo(state.assignToVehicle())
    fun startTransit() = moveTo(state.startTransit())
    fun markDelivered() = moveTo(state.markDelivered())
    fun markFailed() = moveTo(state.markFailed())

    private fun moveTo(next: ShipmentState) {
        log += "${state.name} -> ${next.name}"   // reached only if the state call didn't throw
        state = next
    }
}
