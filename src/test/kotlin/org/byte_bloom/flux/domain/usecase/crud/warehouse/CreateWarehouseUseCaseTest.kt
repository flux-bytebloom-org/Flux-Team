package org.byte_bloom.flux.domain.usecase.crud.warehouse

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.byte_bloom.flux.domain.validator.warehouseValidations.WarehouseCreateValidator
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import org.byte_bloom.flux.domain.testdata.createTestWarehouse

class CreateWarehouseUseCaseTest {

    private val repository = mockk<WarehouseRepository>()
    private val validator = mockk<WarehouseCreateValidator>()
    private val useCase = CreateWarehouseUseCase(repository, validator)

    private val warehouse = createTestWarehouse()

    @Test
    fun `given valid warehouse when create then returns success`() = runTest {
        // Given
        every { validator(warehouse) } returns ValidationResult.Valid
        coEvery { repository.create(warehouse) } returns Result.success(warehouse)

        // When
        val result = useCase(warehouse)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(warehouse)

        coVerify(exactly = 1) {
            repository.create(warehouse)
        }
    }

    @Test
    fun `given invalid warehouse when create then returns EntityValidationException`() = runTest {
        // Given
        val validationError = ValidationResult.Invalid(emptyList())

        every { validator(warehouse) } returns validationError

        // When
        val result = useCase(warehouse)

        // Then
        assertThat(result.isFailure).isTrue()

        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)

        coVerify(exactly = 0) {
            repository.create(any())
        }
    }

    @Test
    fun `given valid warehouse when repository fails then returns repository failure`() = runTest {
        // Given
        val repositoryException = RuntimeException("Database error")

        every { validator(warehouse) } returns ValidationResult.Valid
        coEvery { repository.create(warehouse) } returns Result.failure(repositoryException)

        // When
        val result = useCase(warehouse)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(repositoryException)
    }
}