package org.byte_bloom.flux.domain.state

import org.byte_bloom.flux.domain.exception.LogisticsException

sealed interface ShipmentState {

    val name: String get() = this::class.simpleName ?: "UnknownState"


    fun assignToVehicle(): ShipmentState = illegal("AssignedToVehicleState")
    fun startTransit(): ShipmentState = illegal("InTransitState")
    fun markDelivered(): ShipmentState = illegal("DeliveredState")
    fun markFailed(): ShipmentState = illegal("DeliveryFailedState")

    private fun illegal(target: String): Nothing =
        throw LogisticsException.BusinessLogicException.IllegalStateTransitionException(name, target)
}

data object CreatedState : ShipmentState {
    override fun assignToVehicle(): ShipmentState = AssignedToVehicleState
}

data object AssignedToVehicleState : ShipmentState {
    override fun startTransit(): ShipmentState = InTransitState
}

data object InTransitState : ShipmentState {
    override fun markDelivered(): ShipmentState = DeliveredState
    override fun markFailed(): ShipmentState = DeliveryFailedState
}

data object DeliveredState : ShipmentState

data object DeliveryFailedState : ShipmentState