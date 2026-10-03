package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.testdata.aPackage
import org.byte_bloom.flux.domain.testdata.aWarehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ReroutePackageUseCaseTest {

    private val repository = mockk<PackageRepository>()
    private val useCase = ReroutePackageUseCase(repository)

    private val originHub = aWarehouse(id = "WH-001")
    private val newDestination = aWarehouse(id = "WH-003", name = "New Destination")

    @Test
    fun `reroutes the queued package through the repository and returns its result`() {
        // Given
        val queued = aPackage(id = "PKG-001", origin = originHub)
        originHub.addPackage(queued)
        val rerouted = queued.copy(destinationHub = newDestination)
        every { repository.updatePackageDestination(queued, newDestination) } returns rerouted

        // When
        val result = useCase(originHub, queued, newDestination)

        // Then
        assertThat(result).isSameInstanceAs(rerouted)
        verify(exactly = 1) { repository.updatePackageDestination(queued, newDestination) }
    }

    @Test
    fun `passes the queue's own copy to the repository, matched by id`() {
        // Given: same id, but the caller holds a stale copy (different weight)
        val queued = aPackage(id = "PKG-001", weight = 10.0, origin = originHub)
        val staleCopy = aPackage(id = "PKG-001", weight = 99.0, origin = originHub)
        originHub.addPackage(queued)
        every { repository.updatePackageDestination(queued, newDestination) } returns queued

        // When
        useCase(originHub, staleCopy, newDestination)

        // Then
        verify(exactly = 1) { repository.updatePackageDestination(queued, newDestination) }
    }

    @Test
    fun `throws PackageNotInQueueException and never touches the repository when the queue is empty`() {
        // Given
        val pkg = aPackage(id = "PKG-001", origin = originHub)

        // When
        val error = assertFailsWith<LogisticsException.BusinessLogicException.PackageNotInQueueException> {
            useCase(originHub, pkg, newDestination)
        }

        // Then
        assertThat(error.packageId).isEqualTo("PKG-001")
        assertThat(error.warehouseId).isEqualTo("WH-001")
        verify { repository wasNot Called }
    }

    @Test
    fun `throws PackageNotInQueueException when the package is queued at a different hub`() {
        // Given
        val otherHub = aWarehouse(id = "WH-002")
        val pkg = aPackage(id = "PKG-001", origin = originHub)
        otherHub.addPackage(pkg)

        // When
        val error = assertFailsWith<LogisticsException.BusinessLogicException.PackageNotInQueueException> {
            useCase(originHub, pkg, newDestination)
        }

        // Then
        assertThat(error.warehouseId).isEqualTo("WH-001")
        verify { repository wasNot Called }
    }
}
