package org.byte_bloom.flux.domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.junit.jupiter.api.Test

class DeleteRouteUseCaseTest {

    private val repository = mockk<RouteRepository>()
    private val useCase = DeleteRouteUseCase(repository)

    @Test
    fun `given valid id when delete then returns success`() = runBlocking<Unit> {
        // Given
        coEvery { repository.delete("RT-1") } returns Result.success(Unit)

        // When
        val result = useCase("RT-1")

        // Then
        assertThat(result.isSuccess).isTrue()
        coVerify(exactly = 1) { repository.delete("RT-1") }
    }

    @Test
    fun `given id with wrong prefix when delete then returns EntityValidationException`() = runBlocking<Unit> {
        // Given
        val invalidId = "V-1"

        // When
        val result = useCase(invalidId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.delete(any()) }
    }

    @Test
    fun `given empty id when delete then returns EntityValidationException`() = runBlocking<Unit> {
        // Given
        val emptyId = ""

        // When
        val result = useCase(emptyId)

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.delete(any()) }
    }

    @Test
    fun `given valid id when repository fails then returns repository failure`() = runBlocking<Unit> {
        // Given
        val repositoryException = LogisticsException.DataAccessException.NetworkUnavailableException()
        coEvery { repository.delete("RT-1") } returns Result.failure(repositoryException)

        // When
        val result = useCase("RT-1")

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(repositoryException)
    }
}
