package org.byte_bloom.flux.domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.testdata.createTestRoute
import org.junit.jupiter.api.Test
import org.byte_bloom.flux.domain.usecase.route.GetRouteByIdUseCase

class GetRouteByIdUseCaseTest {

    private val repository = mockk<RouteRepository>()
    private val useCase = GetRouteByIdUseCase(repository)

    private val route = createTestRoute()

    @Test
    fun `given valid id when get route then returns route`() = runBlocking<Unit> {
        // Given
        coEvery { repository.getById("RT-1") } returns Result.success(route)

        // When
        val result = useCase("RT-1")

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(route)
        coVerify(exactly = 1) { repository.getById("RT-1") }
    }

    @Test
    fun `given id with wrong prefix when get route then returns EntityValidationException`() = runBlocking<Unit> {
        // Given
        val invalidId = "WH-1"

        // When
        val result = useCase(invalidId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.getById(any()) }
    }

    @Test
    fun `given blank id when get route then returns EntityValidationException`() = runBlocking<Unit> {
        // Given
        val blankId = " "

        // When
        val result = useCase(blankId)

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.getById(any()) }
    }

    @Test
    fun `given missing route when get route then returns RouteNotFoundException`() = runBlocking<Unit> {
        // Given
        val notFound = LogisticsException.EntityNotFoundException.RouteNotFoundException("RT-99")
        coEvery { repository.getById("RT-99") } returns Result.failure(notFound)

        // When
        val result = useCase("RT-99")

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(notFound)
    }

    @Test
    fun `given valid id when repository fails then returns repository failure`() = runBlocking<Unit> {
        // Given
        val repositoryException = LogisticsException.DataAccessException.NetworkUnavailableException()
        coEvery { repository.getById("RT-1") } returns Result.failure(repositoryException)

        // When
        val result = useCase("RT-1")

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(repositoryException)
    }
}
