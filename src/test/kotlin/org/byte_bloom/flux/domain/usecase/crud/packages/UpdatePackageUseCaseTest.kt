package org.byte_bloom.flux.domain.usecase.crud.packages

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.request.PackageUpdateRequest
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.byte_bloom.flux.domain.testdata.createTestWarehouse
import org.byte_bloom.flux.domain.usecase.crud.pakage.UpdatePackageUseCase
import org.byte_bloom.flux.domain.validator.packagevalidations.PackageUpdateValidator
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class UpdatePackageUseCaseTest {

    private val repository: PackageRepository = mockk()
    private val warehouseRepository: WarehouseRepository = mockk()
    private val useCase = UpdatePackageUseCase(
        repository = repository,
        warehouseRepository = warehouseRepository,
        updateValidator = PackageUpdateValidator()
    )

    private val existing = createTestPackage(id = "PKG-1", weight = 10.0, priority = Priority.STANDARD)

    private fun request(
        weight: Double? = null,
        originHubId: String? = null,
        destinationHubId: String? = null,
        priority: String? = null
    ) = PackageUpdateRequest(weight, originHubId, destinationHubId, priority)

    private fun stubHappyPath() {
        coEvery { repository.getById("PKG-1") } returns Result.success(existing)
        coEvery { warehouseRepository.getAll() } returns
                listOf(createTestWarehouse("WH-1"), createTestWarehouse("WH-9"))
        coEvery { repository.update("PKG-1", any()) } answers { Result.success(secondArg<Package>()) }
    }

    @Test
    fun `given new weight when update then only weight changes`() = runTest {
        // Given
        stubHappyPath()

        // When
        val result = useCase("PKG-1", request(weight = 25.0))

        // Then
        val updated = result.getOrThrow()
        assertThat(updated.weight).isEqualTo(25.0)
        assertThat(updated.priority).isEqualTo(existing.priority)
        assertThat(updated.originHub).isEqualTo(existing.originHub)
    }

    @Test
    fun `given lowercase priority when update then priority is changed`() = runTest {
        // Given
        stubHappyPath()

        // When
        val result = useCase("PKG-1", request(priority = "urgent"))

        // Then
        assertThat(result.getOrThrow().priority).isEqualTo(Priority.URGENT)
    }

    @Test
    fun `given known hub id when update then destination hub is replaced`() = runTest {
        // Given
        stubHappyPath()

        // When
        val result = useCase("PKG-1", request(destinationHubId = "WH-9"))

        // Then
        assertThat(result.getOrThrow().destinationHub.id).isEqualTo("WH-9")
    }

    @Test
    fun `given unknown hub id when update then keeps existing hub (current behavior)`() = runTest {
        // Given
        stubHappyPath()

        // When
        val result = useCase("PKG-1", request(destinationHubId = "WH-404"))

        // Then
        assertThat(result.getOrThrow().destinationHub).isEqualTo(existing.destinationHub)
    }

    @Test
    fun `given id with wrong prefix when update then fails without touching repositories`() = runTest {
        // When
        val result = useCase("V-1", request(weight = 5.0))

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.getById(any()) }
        coVerify(exactly = 0) { repository.update(any(), any()) }
    }

    @Test
    fun `given empty request when update then fails validation without touching repositories`() = runTest {
        // When
        val result = useCase("PKG-1", request())

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.getById(any()) }
    }

    @Test
    fun `given package not found when update then propagates failure and skips update`() = runTest {
        // Given
        val error = LogisticsException.EntityNotFoundException.PackageNotFoundException("PKG-1")
        coEvery { repository.getById("PKG-1") } returns Result.failure(error)

        // When
        val result = useCase("PKG-1", request(weight = 5.0))

        // Then
        assertThat(result.exceptionOrNull()).isSameInstanceAs(error)
        coVerify(exactly = 0) { repository.update(any(), any()) }
    }

    @Test
    fun `given invalid priority string when update then throws IllegalArgumentException (known issue)`() = runTest {
        // Given
        stubHappyPath()

        // When / Then
        assertFailsWith<IllegalArgumentException> {
            useCase("PKG-1", request(priority = "SUPER_FAST"))
        }
    }
}