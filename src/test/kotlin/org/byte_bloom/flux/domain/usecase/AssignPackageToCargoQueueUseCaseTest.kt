package org.byte_bloom.flux.domain.usecase

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.byte_bloom.flux.domain.testdata.createTestWarehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class AssignPackageToCargoQueueUseCaseTest {

    private val packageRepo: PackageRepository = mockk()
    private val useCase = AssignPackageToCargoQueueUseCase(packageRepo)

    private val hub = createTestWarehouse("WH-5")
    private val pkg = createTestPackage(id = "PKG-1")

    @Test
    fun `given package and hub when assign then repository is called once and nothing else`() {
        // Given
        every { packageRepo.updatePackageOriginHub(pkg, hub) } returns createTestPackage(id = "PKG-1", origin = hub)

        // When
        useCase(hub, pkg)

        // Then
        verify(exactly = 1) { packageRepo.updatePackageOriginHub(pkg, hub) }
        confirmVerified(packageRepo)
    }

    @Test
    fun `given repository throws when assign then exception propagates`() {
        // Given
        every { packageRepo.updatePackageOriginHub(pkg, hub) } throws
                LogisticsException.EntityNotFoundException.PackageNotFoundException(pkg.id)

        // When / Then
        assertFailsWith<LogisticsException.EntityNotFoundException.PackageNotFoundException> {
            useCase(hub, pkg)
        }
    }
}