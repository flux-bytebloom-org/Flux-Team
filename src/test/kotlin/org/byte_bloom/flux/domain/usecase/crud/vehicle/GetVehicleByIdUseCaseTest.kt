package org.byte_bloom.flux.domain.usecase.crud.vehicle

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.testdata.aVehicle
import org.byte_bloom.flux.domain.repository.VehicleRepository

class GetVehicleByIdUseCaseTest {

    private val repository = mockk<VehicleRepository>()
    val useCase = GetVehicleByIdUseCase(repository)

    @Test
    fun `returns the vehicle found by the repository when id is valid`() = runBlocking<Unit> {
        // Given
        val vehicle = aVehicle(id = "V-001")
        coEvery { repository.getById("V-001") } returns Result.success(vehicle)

        // When
        val result = useCase("V-001")

        // Then
        assertThat(result.getOrNull()).isEqualTo(vehicle)
    }

    @Test
    fun `fails with EntityValidationException and skips the repository when id has the wrong prefix`() =
        runBlocking<Unit> {
            // When
            val result = useCase("WH-001")

            // Then
            assertThat(result.exceptionOrNull())
                .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
            coVerify { repository wasNot Called }
        }

    @Test
    fun `propagates VehicleNotFoundException from the repository`() = runBlocking<Unit> {
        // Given
        val notFound = LogisticsException.EntityNotFoundException.VehicleNotFoundException("V-404")
        coEvery { repository.getById("V-404") } returns Result.failure(notFound)

        // When
        val result = useCase("V-404")

        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(notFound)
    }



}
