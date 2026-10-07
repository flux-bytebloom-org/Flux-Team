package org.byte_bloom.flux.domain.dispatch

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.state.Shipment

class ExpressDispatchProcessor(
    ledger: VehicleCapacityLedger = VehicleCapacityLedger()
) : BaseDispatchProcessor(ledger) {

    override fun validateCargo(shipment: Shipment, vehicle: Vehicle) =
        ensureCanDispatch(shipment, vehicle)

    override fun reserveVehicleCapacity(shipment: Shipment, vehicle: Vehicle) =
        reserveCapacity(shipment, vehicle)

    override fun updateShipmentState(shipment: Shipment) {
        shipment.assignToVehicle()
        shipment.startTransit()
    }

    // Difference from Standard: Express sends a notification through the hook
    override fun notifyDispatchStatus(shipment: Shipment, vehicle: Vehicle) {
        println("[EXPRESS] Package ${shipment.pkg.id} dispatched on vehicle ${vehicle.id} -> ${shipment.state.name}")
    }
}
