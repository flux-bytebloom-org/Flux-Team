package org.byte_bloom.flux.data.datasource

import org.byte_bloom.flux.data.dataholders.RouteRaw

interface RouteDataSource {
    fun getAll(): List<RouteRaw>
}
