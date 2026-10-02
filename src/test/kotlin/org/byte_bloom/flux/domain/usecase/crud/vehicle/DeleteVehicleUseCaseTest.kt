package org.byte_bloom.flux.domain.usecase.crud.vehicle

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.VehicleRepository

class DeleteVehicleUseCaseTest {
    private val repository = mockk<VehicleRepository>()
    val useCase = DeleteVehicleUseCase(repository)

    @Test
    fun `deletes through the repository when id is valid`() = runBlocking<Unit> {
        // Given
        coEvery { repository.delete("V-001") } returns Result.success(Unit)

        // When
        val result = useCase("V-001")

        // Then
        assertThat(result.isSuccess).isTrue()
        coVerify(exactly = 1) { repository.delete("V-001") }
    }

    @Test
    fun `fails with EntityValidationException and skips the repository when id has the wrong prefix`() =
        runBlocking<Unit> {
            // When
            val result = useCase("PKG-001")

            // Then
            assertThat(result.exceptionOrNull())
                .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
            coVerify { repository wasNot Called }
        }

    @Test
    fun `propagates the repository failure unchanged`() = runBlocking<Unit> {
        // Given
        val failure = LogisticsException.DataAccessException.NetworkUnavailableException()
        coEvery { repository.delete("V-001") } returns Result.failure(failure)

        // When
        val result = useCase("V-001")

        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(failure)
    }

    fun aWarehouse(
        id: String = "WH-001",
        name: String = "Test Hub",
        zone: RegionalZone = RegionalZone.NORTH
    ) = Warehouse(id, name, zone, 0.0, 0.0)

    fun aVehicle(
        id: String = "V-001",
        hub: Warehouse = aWarehouse(),
        capacity: Double = 500.0,
        costPerKm: Double = 2.0
    ) = Vehicle(id, hub, capacity, costPerKm)

}
