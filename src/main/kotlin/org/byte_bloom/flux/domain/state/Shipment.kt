package org.byte_bloom.flux.domain.state

import org.byte_bloom.flux.domain.model.Package

class Shipment(val pkg: Package) {

    var state: ShipmentState = CreatedState
        private set

    fun assignToVehicle() { state = state.assignToVehicle() }
    fun startTransit() { state = state.startTransit() }
    fun markDelivered() { state = state.markDelivered() }
    fun markFailed() { state = state.markFailed() }
}