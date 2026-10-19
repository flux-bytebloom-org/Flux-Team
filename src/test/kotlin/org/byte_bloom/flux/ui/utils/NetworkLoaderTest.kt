package org.byte_bloom.flux.ui.utils

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

private const val CONCURRENT_CALLERS = 5
private const val SLOW_CALL_MILLIS = 50L
private const val FAILURE_DELAY_MILLIS = 10L
private const val VERY_LONG_MILLIS = 5_000L
private const val ONCE = 1

class NetworkLoaderTest {

    private val warehouseRepository = mockk<WarehouseRepository>()
    private val routeRepository = mockk<RouteRepository>()
    private val vehicleRepository = mockk<VehicleRepository>()
    private val packageRepository = mockk<PackageRepository>()

    private val warehouse = Warehouse("WH-001", "Hub", RegionalZone.CENTRAL, 31.5, 34.5)
    private val route = Route("RT-001", warehouse, warehouse, 10.0, 5.0)
    private val vehicle = Vehicle("V-001", warehouse, 100.0, 2.0)
    private val pkg = Package("PKG-001", 5.0, warehouse, warehouse, Priority.STANDARD)

    private val loader = NetworkLoader(
        warehouseRepository, routeRepository, vehicleRepository, packageRepository
    )

    init {
        coEvery { warehouseRepository.getAll() } returns listOf(warehouse)
        coEvery { routeRepository.getAll() } returns listOf(route)
        coEvery { vehicleRepository.getAll() } returns listOf(vehicle)
        coEvery { packageRepository.getAll() } returns listOf(pkg)
    }

    @Test
    fun `given working repositories when load then the snapshot holds everything they returned`() = runBlocking<Unit> {
        val snapshot = loader.load()

        assertThat(snapshot).isEqualTo(
            NetworkSnapshot(
                warehouses = listOf(warehouse),
                routes = listOf(route),
                fleet = listOf(vehicle),
                packages = listOf(pkg)
            )
        )
    }

    @Test
    fun `given load was already called when load again then returns the same snapshot without reloading`() = runBlocking<Unit> {
        val first = loader.load()
        val second = loader.load()

        assertThat(second).isSameInstanceAs(first)
        coVerifyEachRepositoryLoadedOnce()
    }

    @Test
    fun `given several callers at the same time when load then the network is loaded only once`() = runBlocking<Unit> {
        coEvery { routeRepository.getAll() } coAnswers {
            delay(SLOW_CALL_MILLIS)
            listOf(route)
        }

        val snapshots = coroutineScope {
            List(CONCURRENT_CALLERS) { async(Dispatchers.Default) { loader.load() } }.awaitAll()
        }

        snapshots.forEach { assertThat(it).isSameInstanceAs(snapshots.first()) }
        coVerifyEachRepositoryLoadedOnce()
    }

    @Test
    fun `given a repository fails when load then the error propagates`() = runBlocking<Unit> {
        coEvery { routeRepository.getAll() } throws IllegalStateException("boom")

        assertFailsWith<IllegalStateException> { loader.load() }
    }

    @Test
    fun `given a repository fails when load then the other running calls are cancelled`() = runBlocking<Unit> {
        var fleetCallWasCancelled = false
        coEvery { routeRepository.getAll() } coAnswers {
            delay(FAILURE_DELAY_MILLIS)
            throw IllegalStateException("boom")
        }
        coEvery { vehicleRepository.getAll() } coAnswers {
            try {
                delay(VERY_LONG_MILLIS)
                listOf(vehicle)
            } catch (cancellation: CancellationException) {
                fleetCallWasCancelled = true
                throw cancellation
            }
        }

        assertFailsWith<IllegalStateException> { loader.load() }

        assertThat(fleetCallWasCancelled).isTrue()
    }

    private fun coVerifyEachRepositoryLoadedOnce() {
        coVerify(exactly = ONCE) { warehouseRepository.getAll() }
        coVerify(exactly = ONCE) { routeRepository.getAll() }
        coVerify(exactly = ONCE) { vehicleRepository.getAll() }
        coVerify(exactly = ONCE) { packageRepository.getAll() }
    }
}
