package org.byte_bloom.flux.domain.repository
import org.byte_bloom.flux.data.dataholders.RouteRaw

interface RouteRepository {
    fun getAll(): List<RouteRaw>
}
