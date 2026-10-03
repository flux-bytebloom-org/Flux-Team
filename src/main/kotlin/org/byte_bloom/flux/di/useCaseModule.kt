package org.byte_bloom.flux.di

import org.byte_bloom.flux.domain.algorithm.routing.BreadthFirstRouter
import org.byte_bloom.flux.domain.algorithm.routing.DijkstraRouter
import org.byte_bloom.flux.domain.usecase.*
import org.byte_bloom.flux.domain.usecase.crud.pakage.*
import org.byte_bloom.flux.domain.usecase.crud.route.*
import org.byte_bloom.flux.domain.usecase.crud.vehicle.*
import org.byte_bloom.flux.domain.usecase.crud.warehouse.*
import org.koin.dsl.module

val useCaseModule = module {


    // ===== Package CRUD =====
    factory { CreatePackageUseCase(get(), get()) }
    factory { GetPackageByIdUseCase(get()) }
    factory { DeletePackageUseCase(get()) }
    factory { UpdatePackageUseCase(get(), get(), updateValidator = get()) }

    // ===== Route CRUD =====
    factory { CreateRouteUseCase(get(), get()) }
    factory { GetRouteByIdUseCase(get()) }
    factory { DeleteRouteUseCase(get()) }
    factory { UpdateRouteUseCase(get(), get()) }

    // ===== Vehicle CRUD =====
    factory { CreateVehicleUseCase(get(), get()) }
    factory { GetVehicleByIdUseCase(get()) }
    factory { DeleteVehicleUseCase(get()) }
    factory { UpdateVehicleUseCase(get(), get(), updateValidator = get()) }

    // ===== Warehouse CRUD =====
    factory { CreateWarehouseUseCase(get(), get()) }
    factory { GetWarehouseByIdUseCase(get()) }
    factory { DeleteWarehouseUseCase(get()) }
    factory { UpdateWarehouseUseCase(get()) }

    // ===== Routing =====
    factory { FindOptimalPathUseCase(get()) }
    factory { FindFastestPathUseCase(get()) }
    factory { FindFewestHopsRouteUseCase(get()) }
    factory { DecideRoutingWayUseCase(get(), get()) }

    // ===== Dispatch / Fleet =====
    factory { DispatchVehicleUseCase(get(), get()) }
    factory { GreedyFleetDispatchUseCase() }
    factory { AddVehicleToHubUseCase(get()) }
    factory { AssignPackageToCargoQueueUseCase(get()) }
    factory { RemovePackageFromQueueUseCase(get()) }
    factory { ReroutePackageUseCase(get()) }
    factory { SplitAndRerouteShipmentsUseCase(get()) }

    // ===== Vehicle/Package Matching =====
    factory { FindSmallestFitVehicleUseCase() }
    factory { FindStationedVehiclesByCapacityUseCase() }
    factory { AssignPackageToLowestCostStationedVehicleUseCase(get()) }

    // ===== Pricing =====
    factory { CalculatePricingUseCase(get()) }

    // ===== Load Balancing / Bottleneck Analysis =====
    factory { GetWarehouseLoadFactorUseCase() }
    factory { CalculateRebalanceRatioUseCase() }
    factory { ClassifyTripUrgencyUseCase() }
    factory { ExtractUniqueShipmentRoutesUseCase() }
    factory { GenerateWeightedShipmentPathsUseCase(get()) }
    factory { FindBottleneckWarehousesUseCase() }
    factory { FilterActualAlternativePathsUseCase() }
    factory { PenalizeBottleneckRoutesUseCase(get()) }

    // ===== Tree / Analytics =====
    factory { AnalyzeTreePerformanceUseCase() }
    factory { TraceHubLineageUseCase() }
}
