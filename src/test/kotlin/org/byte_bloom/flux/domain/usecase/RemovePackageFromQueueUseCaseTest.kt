package org.byte_bloom.flux.domain.usecase

import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.testdata.aPackage
import org.byte_bloom.flux.domain.testdata.aWarehouse
import org.junit.jupiter.api.Test

class RemovePackageFromQueueUseCaseTest {

    private val repository = mockk<PackageRepository>()
    private val useCase = RemovePackageFromQueueUseCase(repository)

    @Test
    fun `asks the repository to remove the package from the given hub`() {
        // Given
        val hub = aWarehouse(id = "WH-001")
        val pkg = aPackage(id = "PKG-001", origin = hub)
        justRun { repository.removePackageFromHub(pkg, hub) }

        // When
        useCase(hub, pkg)

        // Then
        verify(exactly = 1) { repository.removePackageFromHub(pkg, hub) }
    }
}
