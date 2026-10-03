package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.response.DispatchedVehicle
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.byte_bloom.flux.domain.testdata.createTestVehicle
import org.byte_bloom.flux.domain.testdata.createTestWarehouse
import org.junit.jupiter.api.Test

class DispatchVehicleUseCaseTest {

    private val packageRepo: PackageRepository = mockk()
    private val findOptimalPath: FindOptimalPathUseCase = mockk()
    private val useCase = DispatchVehicleUseCase(packageRepo, findOptimalPath)

    private val hub = createTestWarehouse("WH-1")
    private val destA = createTestWarehouse("WH-2")
    private val destB = createTestWarehouse("WH-3")
    private val vehicle = createTestVehicle(hub = hub, maxCapacityKg = 100.0)

    init {
        justRun { packageRepo.removePackageFromHub(any(), any()) }
    }

    private fun queuedPackage(
        id: String,
        destination: Warehouse,
        weight: Double = 10.0,
        priority: Priority = Priority.STANDARD
    ): Package = createTestPackage(
        id = id,
        weight = weight,
        origin = hub,
        destination = destination,
        priority = priority
    ).also { hub.addPackage(it) }

    @Test
    fun `given empty cargo queue when dispatch then returns empty result without routing`() {
        // Given: hub has no packages

        // When
        val result = useCase(hub, vehicle)

        // Then
        assertThat(result).isEqualTo(
            DispatchedVehicle(vehicle, emptyList(), 0.0, emptyList())
        )
        verify(exactly = 0) { findOptimalPath(any(), any()) }
        verify(exactly = 0) { packageRepo.removePackageFromHub(any(), any()) }
    }

    @Test
    fun `given packages for one destination when dispatch then loads them with total weight and path`() {
        // Given
        val p1 = queuedPackage("PKG-1", destA, weight = 10.0)
        val p2 = queuedPackage("PKG-2", destA, weight = 25.0)
        every { findOptimalPath(hub, destA) } returns listOf(hub, destA)

        // When
        val result = useCase(hub, vehicle)

        // Then
        assertThat(result.vehicle).isEqualTo(vehicle)
        assertThat(result.loadedPackages).containsExactly(p1, p2)
        assertThat(result.totalWeight).isEqualTo(35.0)
        assertThat(result.path).containsExactly(hub, destA).inOrder()
    }

    @Test
    fun `given loaded packages when dispatch then removes each from repository and from hub queue`() {
        // Given
        val p1 = queuedPackage("PKG-1", destA)
        val p2 = queuedPackage("PKG-2", destA)
        every { findOptimalPath(hub, destA) } returns listOf(hub, destA)

        // When
        useCase(hub, vehicle)

        // Then
        verify(exactly = 1) { packageRepo.removePackageFromHub(p1, hub) }
        verify(exactly = 1) { packageRepo.removePackageFromHub(p2, hub) }
        assertThat(hub.getCargoQueue()).isEmpty()
    }

    @Test
    fun `given packages for two destinations when dispatch then targets destination with most packages`() {
        // Given
        val toA1 = queuedPackage("PKG-1", destA)
        val toA2 = queuedPackage("PKG-2", destA)
        val toB = queuedPackage("PKG-3", destB)
        every { findOptimalPath(hub, destA) } returns listOf(hub, destA)

        // When
        val result = useCase(hub, vehicle)

        // Then
        verify(exactly = 1) { findOptimalPath(hub, destA) }
        verify(exactly = 0) { findOptimalPath(hub, destB) }
        assertThat(result.loadedPackages).containsExactly(toA1, toA2)
        assertThat(hub.getCargoQueue()).containsExactly(toB)
    }
}