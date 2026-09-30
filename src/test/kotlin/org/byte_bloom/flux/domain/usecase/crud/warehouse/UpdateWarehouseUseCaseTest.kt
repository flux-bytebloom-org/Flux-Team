package org.byte_bloom.flux.domain.usecase.crud.warehouse

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.request.WarehouseUpdateRequest
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.byte_bloom.flux.domain.validator.warehouseValidations.WarehouseUpdateValidator
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import org.byte_bloom.flux.domain.testdata.createTestWarehouse

class UpdateWarehouseUseCaseTest {

    private val repository = mockk<WarehouseRepository>()
    private val validator = mockk<WarehouseUpdateValidator>()

    private val useCase = UpdateWarehouseUseCase(
        repository = repository,
        validator = validator
    )

    private val existingWarehouse = createTestWarehouse()
    @Test
    fun `given valid id and request when update then returns updated warehouse`() = runTest {
        // Given
        val request = WarehouseUpdateRequest(
            name = "Updated Warehouse",
            regionalZone = "NORTH",
            latitude = 33.0,
            longitude = 36.0
        )

        val expectedWarehouse = existingWarehouse.copy(
            name = "Updated Warehouse",
            regionalZone = RegionalZone.NORTH,
            latitude = 33.0,
            longitude = 36.0
        )

        every { validator(request) } returns ValidationResult.Valid
        coEvery {
            repository.getById("WH-1")
        } returns Result.success(existingWarehouse)

        coEvery {
            repository.update("WH-1", expectedWarehouse)
        } returns Result.success(expectedWarehouse)

        // When
        val result = useCase("WH-1", request)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedWarehouse)

        coVerify(exactly = 1) {
            repository.getById("WH-1")
        }

        coVerify(exactly = 1) {
            repository.update("WH-1", expectedWarehouse)
        }
    }

    @Test
    fun `given invalid id when update then returns EntityValidationException`() = runTest {
        // Given
        val request = WarehouseUpdateRequest(
            name = "Updated Warehouse",
            regionalZone = null,
            latitude = null,
            longitude = null
        )

        // When
        val result = useCase("WRONG-1", request)

        // Then
        assertThat(result.isFailure).isTrue()

        assertThat(result.exceptionOrNull())
            .isInstanceOf(
                LogisticsException.ValidationException.EntityValidationException::class.java
            )

        coVerify(exactly = 0) {
            repository.getById(any())
        }

        coVerify(exactly = 0) {
            repository.update(any(), any())
        }
    }

    @Test
    fun `given invalid update request when update then throws IllegalArgumentException`() = runTest {
        // Given
        val request = WarehouseUpdateRequest(
            name = " ",
            regionalZone = null,
            latitude = null,
            longitude = null
        )

        every { validator(request) } returns ValidationResult.Invalid(emptyList())

        // When
        val exception = try {
            useCase("WH-1", request)
            null
        } catch (e: IllegalArgumentException) {
            e
        }

        // Then
        assertThat(exception).isNotNull()

        coVerify(exactly = 0) {
            repository.getById(any())
        }

        coVerify(exactly = 0) {
            repository.update(any(), any())
        }
    }

    @Test
    fun `given valid request when repository get fails then returns repository failure`() = runTest {
        // Given
        val request = WarehouseUpdateRequest(
            name = "Updated Warehouse",
            regionalZone = null,
            latitude = null,
            longitude = null
        )

        val repositoryException =
            LogisticsException.DataAccessException.NetworkUnavailableException()

        every { validator(request) } returns ValidationResult.Valid

        coEvery {
            repository.getById("WH-1")
        } returns Result.failure(repositoryException)

        // When
        val result = useCase("WH-1", request)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(repositoryException)

        coVerify(exactly = 0) {
            repository.update(any(), any())
        }
    }

    @Test
    fun `given valid request when repository update fails then returns repository failure`() = runTest {
        // Given
        val request = WarehouseUpdateRequest(
            name = "Updated Warehouse",
            regionalZone = null,
            latitude = null,
            longitude = null
        )

        val updatedWarehouse = existingWarehouse.copy(
            name = "Updated Warehouse"
        )

        val repositoryException =
            LogisticsException.DataAccessException.DatabaseConflictException(
                "Update conflict"
            )

        every { validator(request) } returns ValidationResult.Valid

        coEvery {
            repository.getById("WH-1")
        } returns Result.success(existingWarehouse)

        coEvery {
            repository.update("WH-1", updatedWarehouse)
        } returns Result.failure(repositoryException)

        // When
        val result = useCase("WH-1", request)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(repositoryException)
    }
}