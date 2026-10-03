package org.byte_bloom.flux.domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.testdata.createTestRoute
import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.byte_bloom.flux.domain.validator.routevalidation.RouteCreateValidator
import org.junit.jupiter.api.Test

class CreateRouteUseCaseTest {

    private val repository = mockk<RouteRepository>()
    private val validator = mockk<RouteCreateValidator>()
    private val useCase = CreateRouteUseCase(repository, validator)

    private val route = createTestRoute()

    @Test
    fun `given valid route when create then returns created route`() = runBlocking<Unit> {
        // Given
        every { validator(route) } returns ValidationResult.Valid
        coEvery { repository.create(route) } returns Result.success(route)

        // When
        val result = useCase(route)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(route)
        coVerify(exactly = 1) { repository.create(route) }
    }

    @Test
    fun `given invalid route when create then returns EntityValidationException`() = runBlocking<Unit> {
        // Given
        val invalid = ValidationResult.Invalid(
            listOf(ValidationField.NotPositive(EntityField.DISTANCE_KM, 0.0))
        )
        every { validator(route) } returns invalid

        // When
        val result = useCase(route)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.create(any()) }
    }

    @Test
    fun `given valid route when repository fails then returns repository failure`() = runBlocking<Unit> {
        // Given
        val repositoryException = LogisticsException.DataAccessException.NetworkUnavailableException()
        every { validator(route) } returns ValidationResult.Valid
        coEvery { repository.create(route) } returns Result.failure(repositoryException)

        // When
        val result = useCase(route)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(repositoryException)
    }
}
