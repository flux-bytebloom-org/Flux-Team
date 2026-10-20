package org.byte_bloom.flux.domain.telemetry

import kotlinx.coroutines.flow.Flow
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.VehicleTelemetry

interface TelemetryStreamingService {
    fun stream(vehicles: List<Vehicle>): Flow<VehicleTelemetry>
}