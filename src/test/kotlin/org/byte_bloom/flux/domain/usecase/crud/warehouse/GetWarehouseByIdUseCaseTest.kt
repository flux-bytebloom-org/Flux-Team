package org.byte_bloom.flux.domain.usecase.crud.warehouse

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import org.byte_bloom.flux.domain.testdata.createTestWarehouse

class GetWarehouseByIdUseCaseTest {

    private val repository = mockk<WarehouseRepository>()
    private val useCase =
        org.byte_bloom.flux.domain.usecase.warehouse.GetWarehouseByIdUseCase(repository)

    private val warehouse = createTestWarehouse()
    @Test
    fun `given valid id when get warehouse then returns success`() = runTest {
        // Given
        coEvery { repository.getById("WH-1") } returns Result.success(warehouse)

        // When
        val result = useCase("WH-1")

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(warehouse)
    }

    @Test
    fun `given invalid id when get warehouse then throws IllegalArgumentException`() = runTest {
        // Given
        val invalidId = "WRONG-1"

        // When
        val exception = try {
            useCase(invalidId)
            null
        } catch (e: IllegalArgumentException) {
            e
        }

        // Then
        assertThat(exception).isNotNull()

        coVerify(exactly = 0) {
            repository.getById(any())
        }
    }

    @Test
    fun `given valid id when repository fails then returns repository failure`() = runTest {
        // Given
        val repositoryException = LogisticsException.DataAccessException.NetworkUnavailableException()

        coEvery {
            repository.getById("WH-1")
        } returns Result.failure(repositoryException)

        // When
        val result = useCase("WH-1")

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(repositoryException)
    }
}