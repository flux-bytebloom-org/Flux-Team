package org.byte_bloom.flux.ui.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository


data class NetworkSnapshot(
    val warehouses: List<Warehouse>,
    val routes: List<Route>,
    val fleet: List<Vehicle>,
    val packages: List<Package>
)

class NetworkLoader(
    private val warehouseRepository: WarehouseRepository,
    private val routeRepository: RouteRepository,
    private val vehicleRepository: VehicleRepository,
    private val packageRepository: PackageRepository
) {
    private val mutex = Mutex()
    var cached: NetworkSnapshot? = null

    val x = 10

    suspend fun load(): NetworkSnapshot = mutex.withLock {
        cached ?: loadFresh().also { cached = it }
    }

    private suspend fun loadFresh(): NetworkSnapshot {
        // Must finish first: the other repositories read the cached warehouses and attach to them.
        val warehouses = warehouseRepository.getAll()

        // Independent calls run in parallel. If one fails, coroutineScope cancels the others.
        return coroutineScope {
            val routes = async { routeRepository.getAll() }
            val fleet = async { vehicleRepository.getAll() }
            val packages = async { packageRepository.getAll() }

            NetworkSnapshot(
                warehouses = warehouses,
                routes = routes.await(),
                fleet = fleet.await(),
                packages = packages.await()
            )
        }
    }

}
