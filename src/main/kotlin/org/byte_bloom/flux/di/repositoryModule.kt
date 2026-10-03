package org.byte_bloom.flux.di

import org.byte_bloom.flux.data.csv.datasource.CsvPackageDataSource
import org.byte_bloom.flux.data.csv.datasource.CsvRouteDataSource
import org.byte_bloom.flux.data.csv.datasource.CsvVehicleDataSource
import org.byte_bloom.flux.data.csv.datasource.CsvWarehouseDataSource
import org.byte_bloom.flux.data.csv.datasource.PackageDataSource
import org.byte_bloom.flux.data.csv.datasource.RouteDataSource
import org.byte_bloom.flux.data.csv.datasource.VehicleDataSource
import org.byte_bloom.flux.data.csv.datasource.WarehouseDataSource
import org.byte_bloom.flux.data.repositoryimplementation.PackageRepositoryImpl
import org.byte_bloom.flux.data.repositoryimplementation.RouteRepositoryImpl
import org.byte_bloom.flux.data.repositoryimplementation.VehicleRepositoryImpl
import org.byte_bloom.flux.data.repositoryimplementation.WarehouseRepositoryImpl
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.koin.dsl.module

private const val WAREHOUSES_CSV_PATH = "src/main/resources/warehouses.csv"
private const val PACKAGES_CSV_PATH = "src/main/resources/packages.csv"
private const val ROUTES_CSV_PATH = "src/main/resources/routes.csv"
private const val FLEET_CSV_PATH = "src/main/resources/fleet.csv"

val repositoryModule = module {

    single<WarehouseDataSource> {
        CsvWarehouseDataSource(WAREHOUSES_CSV_PATH)
    }

    single<PackageDataSource> {
        CsvPackageDataSource(PACKAGES_CSV_PATH)
    }

    single<RouteDataSource> {
        CsvRouteDataSource(ROUTES_CSV_PATH)
    }

    single<VehicleDataSource> {
        CsvVehicleDataSource(FLEET_CSV_PATH)
    }

    single<WarehouseRepository> {
        WarehouseRepositoryImpl(
            localDataSource = get(),
            remoteDataSource = get()
        )
    }

    single<RouteRepository> {
        RouteRepositoryImpl(
            localRouteDataSource = get(),
            remoteRouteDataSource = get(),
            warehouseRepository = get()
        )
    }

    single<VehicleRepository> {
        VehicleRepositoryImpl(
            localDataSource = get(),
            remoteDataSource = get(),
            warehouseRepository = get()
        )
    }

    single<PackageRepository> {
        PackageRepositoryImpl(
            pkgDataSource = get(),
            remoteDataSource = get(),
            warehouseRepository = get()
        )
    }
}