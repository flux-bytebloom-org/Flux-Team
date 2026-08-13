package org.byte_bloom.flux.domain.repository
import org.byte_bloom.flux.data.dataholders.VehicleRaw
interface VehicleRepository {
    fun getAll(): List<VehicleRaw>
}