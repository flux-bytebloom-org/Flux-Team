package org.byte_bloom.flux.data.csv.datasource

import org.byte_bloom.flux.data.csv.dataholders.RouteRaw

interface RouteDataSource {
    fun getAll(): List<RouteRaw>
}
