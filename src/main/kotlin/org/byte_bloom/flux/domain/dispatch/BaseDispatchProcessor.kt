package org.byte_bloom.flux.domain.dispatch

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.state.CreatedState
import org.byte_bloom.flux.domain.state.Shipment

abstract class BaseDispatchProcessor(
    private val ledger: VehicleCapacityLedger = VehicleCapacityLedger()
) {

    private val reservedKg = mutableMapOf<String, Double>()

    // Template method: the order of the steps is fixed
    fun process(shipment: Shipment, vehicle: Vehicle) {
        validateCargo(shipment, vehicle)
        reserveVehicleCapacity(shipment, vehicle)
        updateShipmentState(shipment)
        notifyDispatchStatus(shipment, vehicle)
    }

    protected abstract fun validateCargo(shipment: Shipment, vehicle: Vehicle)
    protected abstract fun reserveVehicleCapacity(shipment: Shipment, vehicle: Vehicle)
    protected abstract fun updateShipmentState(shipment: Shipment)

    // Hook: optional step, empty by default
    protected open fun notifyDispatchStatus(shipment: Shipment, vehicle: Vehicle) {}

    protected fun ensureCanDispatch(shipment: Shipment, vehicle: Vehicle) {

       if (shipment.state != CreatedState) {
            throw LogisticsException.BusinessLogicException.IllegalStateTransitionException(
                shipment.state.name, "AssignedToVehicleState"
            )
        }

        val weight = shipment.pkg.weight
        if (weight == null || !weight.isFinite() || weight <= 0.0) {
            throw LogisticsException.ValidationException.InvalidPackageWeightException(
                "package ${shipment.pkg.id} has invalid weight: $weight"
            )
        }
        val remaining = ledger.remainingKg(vehicle)
        if (weight > remaining) {
            throw LogisticsException.BusinessLogicException.VehicleCapacityExceededException(
                "package ${shipment.pkg.id} ($weight kg) exceeds remaining capacity ($remaining kg) of vehicle ${vehicle.id}"
            )
        }
    }

    protected fun reserveCapacity(shipment: Shipment, vehicle: Vehicle) {
        ledger.reserve(vehicle, shipment.pkg.weight ?: 0.0)
    }
}
