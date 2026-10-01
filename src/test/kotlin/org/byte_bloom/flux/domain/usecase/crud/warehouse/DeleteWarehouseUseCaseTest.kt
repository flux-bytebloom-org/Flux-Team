package org.byte_bloom.flux.domain.usecase.crud.warehouse

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest

class DeleteWarehouseUseCaseTest {

    private val repository = mockk<WarehouseRepository>()
    private val useCase = DeleteWarehouseUseCase(repository)

    @Test
    fun `given valid id when delete then returns success`() = runTest {
        // Given
        coEvery {
            repository.delete("WH-1")
        } returns Result.success(Unit)

        // When
        val result = useCase("WH-1")

        // Then
        assertThat(result.isSuccess).isTrue()

        coVerify(exactly = 1) {
            repository.delete("WH-1")
        }
    }

    @Test
    fun `given invalid id when delete then returns EntityValidationException`() = runTest {
        // Given
        val invalidId = "WRONG-1"

        // When
        val result = useCase(invalidId)

        // Then
        assertThat(result.isFailure).isTrue()

        assertThat(result.exceptionOrNull())
            .isInstanceOf(
                LogisticsException.ValidationException.EntityValidationException::class.java
            )

        coVerify(exactly = 0) {
            repository.delete(any())
        }
    }

    @Test
    fun `given valid id when repository fails then returns repository failure`() = runTest {
        // Given
        val repositoryException =
            LogisticsException.DataAccessException.NetworkUnavailableException()

        coEvery {
            repository.delete("WH-1")
        } returns Result.failure(repositoryException)

        // When
        val result = useCase("WH-1")

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(repositoryException)
    }
}