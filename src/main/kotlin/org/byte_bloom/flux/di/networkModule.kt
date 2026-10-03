package org.byte_bloom.flux.di

import io.ktor.client.HttpClient
import org.byte_bloom.flux.data.remote.client.SupabaseHttpClient
import org.byte_bloom.flux.data.remote.datasource.RemotePackageDataSource
import org.byte_bloom.flux.data.remote.datasource.RemoteRouteDataSource
import org.byte_bloom.flux.data.remote.datasource.RemoteVehicleDataSource
import org.byte_bloom.flux.data.remote.datasource.WarehouseDataSource
import org.byte_bloom.flux.data.remote.datasource.impl.SupabasePackageDataSource
import org.byte_bloom.flux.data.remote.datasource.impl.SupabaseRouteDataSource
import org.byte_bloom.flux.data.remote.datasource.impl.SupabaseVehicleDataSource
import org.byte_bloom.flux.data.remote.datasource.impl.SupabaseWarehouseDataSource
import org.koin.dsl.module

val networkModule = module {

    single<HttpClient> {
        SupabaseHttpClient.client
    }

    single<RemotePackageDataSource> {
        SupabasePackageDataSource(
            client = get()
        )
    }

    single<RemoteRouteDataSource> {
        SupabaseRouteDataSource(
            client = get()
        )
    }

    single<RemoteVehicleDataSource> {
        SupabaseVehicleDataSource(
            client = get()
        )
    }

    single<WarehouseDataSource> {
        SupabaseWarehouseDataSource(
            client = get()
        )
    }
}