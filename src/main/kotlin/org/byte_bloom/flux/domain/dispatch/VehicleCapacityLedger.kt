package org.byte_bloom.flux.domain.dispatch

import org.byte_bloom.flux.domain.model.Vehicle

class VehicleCapacityLedger {

    private val reservedKg = mutableMapOf<String, Double>()

    fun remainingKg(vehicle: Vehicle): Double =
        vehicle.maxCapacityKg - (reservedKg[vehicle.id] ?: 0.0)

    fun reserve(vehicle: Vehicle, kg: Double) {
        reservedKg[vehicle.id] = (reservedKg[vehicle.id] ?: 0.0) + kg
    }

    /** Frees the vehicle once it has completed its trip. */
    fun release(vehicle: Vehicle) {
        reservedKg.remove(vehicle.id)
    }
}
