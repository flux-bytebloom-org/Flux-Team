package org.byte_bloom.flux.domain.repository

import org.byte_bloom.flux.domain.model.Vehicle

interface VehicleRepository {
    fun getAll(): List<Vehicle>
}

