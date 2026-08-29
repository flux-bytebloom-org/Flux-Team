package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

class AddVehicleToHubUseCase {

    operator fun invoke(hub: Warehouse, vehicle: Vehicle) {
        val updatedVehicle = vehicle.copy(currentHub = hub)
        hub.addVehicle(updatedVehicle)
    }
}
