package org.byte_bloom.flux.domain.dispatch

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.state.Shipment

class StandardDispatchProcessor : BaseDispatchProcessor() {

    override fun validateCargo(shipment: Shipment, vehicle: Vehicle) =
        ensureCanDispatch(shipment, vehicle)

    override fun reserveVehicleCapacity(shipment: Shipment, vehicle: Vehicle) =
        reserveCapacity(shipment, vehicle)

    override fun updateShipmentState(shipment: Shipment) {
        shipment.assignToVehicle()
        shipment.startTransit()
    }
}
