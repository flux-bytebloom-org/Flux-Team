package org.byte_bloom.flux.di

import org.byte_bloom.flux.domain.usecase.*
import org.byte_bloom.flux.domain.usecase.crud.pakage.*
import org.byte_bloom.flux.domain.usecase.crud.route.*
import org.byte_bloom.flux.domain.usecase.crud.vehicle.*
import org.byte_bloom.flux.domain.usecase.crud.warehouse.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.java.KoinJavaComponent.getKoin

class KoinModulesIntegrationTest {

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `all modules load together without missing definitions`() {
        assertDoesNotThrow {
            startKoin {
                modules(networkModule, repositoryModule, validatorModule, useCaseModule)
            }

            // ===== Package CRUD =====
            val createPackage: CreatePackageUseCase = getKoin().get()
            val getPackageById: GetPackageByIdUseCase = getKoin().get()
            val deletePackage: DeletePackageUseCase = getKoin().get()
            val updatePackage: UpdatePackageUseCase = getKoin().get()

            // ===== Route CRUD =====
            val createRoute: CreateRouteUseCase = getKoin().get()
            val getRouteById: GetRouteByIdUseCase = getKoin().get()
            val deleteRoute: DeleteRouteUseCase = getKoin().get()
            val updateRoute: UpdateRouteUseCase = getKoin().get()

            // ===== Vehicle CRUD =====
            val createVehicle: CreateVehicleUseCase = getKoin().get()
            val getVehicleById: GetVehicleByIdUseCase = getKoin().get()
            val deleteVehicle: DeleteVehicleUseCase = getKoin().get()
            val updateVehicle: UpdateVehicleUseCase = getKoin().get()

            // ===== Warehouse CRUD =====
            val createWarehouse: CreateWarehouseUseCase = getKoin().get()
            val getWarehouseById: GetWarehouseByIdUseCase = getKoin().get()
            val deleteWarehouse: DeleteWarehouseUseCase = getKoin().get()
            val updateWarehouse: UpdateWarehouseUseCase = getKoin().get()

            // ===== Routing =====
            val findOptimalPath: FindOptimalPathUseCase = getKoin().get()
            val findFastestPath: FindFastestPathUseCase = getKoin().get()
            val findFewestHopsRoute: FindFewestHopsRouteUseCase = getKoin().get()
            val decideRoutingWay: DecideRoutingWayUseCase = getKoin().get()

            // ===== Dispatch / Fleet =====
            val dispatchVehicle: DispatchVehicleUseCase = getKoin().get()
            val greedyFleetDispatch: GreedyFleetDispatchUseCase = getKoin().get()
            val addVehicleToHub: AddVehicleToHubUseCase = getKoin().get()
            val assignPackageToCargoQueue: AssignPackageToCargoQueueUseCase = getKoin().get()
            val removePackageFromQueue: RemovePackageFromQueueUseCase = getKoin().get()
            val reroutePackage: ReroutePackageUseCase = getKoin().get()
            val splitAndRerouteShipments: SplitAndRerouteShipmentsUseCase = getKoin().get()

            // ===== Vehicle/Package Matching =====
            val findSmallestFitVehicle: FindSmallestFitVehicleUseCase = getKoin().get()
            val findStationedVehiclesByCapacity: FindStationedVehiclesByCapacityUseCase = getKoin().get()
            val assignPackageToLowestCostVehicle: AssignPackageToLowestCostStationedVehicleUseCase = getKoin().get()

            // ===== Pricing =====
            val calculatePricing: CalculatePricingUseCase = getKoin().get()

            // ===== Load Balancing / Bottleneck Analysis =====
            val getWarehouseLoadFactor: GetWarehouseLoadFactorUseCase = getKoin().get()
            val calculateRebalanceRatio: CalculateRebalanceRatioUseCase = getKoin().get()
            val classifyTripUrgency: ClassifyTripUrgencyUseCase = getKoin().get()
            val extractUniqueShipmentRoutes: ExtractUniqueShipmentRoutesUseCase = getKoin().get()
            val generateWeightedShipmentPaths: GenerateWeightedShipmentPathsUseCase = getKoin().get()
            val findBottleneckWarehouses: FindBottleneckWarehousesUseCase = getKoin().get()
            val filterActualAlternativePaths: FilterActualAlternativePathsUseCase = getKoin().get()
            val penalizeBottleneckRoutes: PenalizeBottleneckRoutesUseCase = getKoin().get()

            // ===== Tree / Analytics =====
            val analyzeTreePerformance: AnalyzeTreePerformanceUseCase = getKoin().get()
            val traceHubLineage: TraceHubLineageUseCase = getKoin().get()
        }
    }
}
