package org.byte_bloom.flux.di

import io.ktor.client.HttpClient
import org.byte_bloom.flux.data.remote.client.SupabaseHttpClient
import org.byte_bloom.flux.data.remote.datasource.RemotePackageDataSource
import org.byte_bloom.flux.data.remote.datasource.RemoteRouteDataSource
import org.byte_bloom.flux.data.remote.datasource.RemoteVehicleDataSource
import org.byte_bloom.flux.data.remote.datasource.RemoteWarehouseDataSource
import org.byte_bloom.flux.data.remote.datasource.impl.SupabasePackageDataSource
import org.byte_bloom.flux.data.remote.datasource.impl.SupabaseRouteDataSource
import org.byte_bloom.flux.data.remote.datasource.impl.SupabaseVehicleDataSource
import org.byte_bloom.flux.data.remote.datasource.impl.SupabaseWarehouseDataSource
import org.byte_bloom.flux.data.remote.middleware.ResiliencePipeline
import org.byte_bloom.flux.data.remote.resilience.circuitbreaker.CircuitBreaker
import org.koin.dsl.module

val networkModule = module {

    single<HttpClient> {
        SupabaseHttpClient.client
    }

    single<RemotePackageDataSource> {
        SupabasePackageDataSource(client = get(), pipeline = get())
    }

    single<RemoteRouteDataSource> {
        SupabaseRouteDataSource(client = get(), pipeline = get())
    }

    single<RemoteVehicleDataSource> {
        SupabaseVehicleDataSource(client = get(), pipeline = get())
    }

    single<RemoteWarehouseDataSource> {
        SupabaseWarehouseDataSource(client = get(), pipeline = get())
    }

    single<CircuitBreaker> { CircuitBreaker() }

    single {
        ResiliencePipeline(
            listOf(

            )
        )
    }
}
