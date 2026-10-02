package org.byte_bloom.flux.domain.usecase.crud.vehicle

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.request.VehicleUpdateRequest
import org.byte_bloom.flux.domain.validator.vehicleValidation.VehicleUpdateValidator

class UpdateVehicleUseCaseTest {
    private val vehicleRepository = mockk<VehicleRepository>()
    private val warehouseRepository = mockk<WarehouseRepository>()
    val useCase = UpdateVehicleUseCase(
        vehicleRepository,
        warehouseRepository = warehouseRepository,
        updateValidator = VehicleUpdateValidator()
    )

    private val hub1 = aWarehouse(id = "WH-001")
    private val hub2 = aWarehouse(id = "WH-002", name = "Second Hub")
    private val existing = aVehicle(id = "V-001", hub = hub1, capacity = 500.0, costPerKm = 2.0)

    // ---------- nothing reaches the repositories ----------

    @Test
    fun `fails and touches no repository when id is invalid`() = runBlocking<Unit> {
        // When
        val result = useCase("bad-id", aRequest(maxCapacityKg = 900.0))

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify { vehicleRepository wasNot Called }
        coVerify { warehouseRepository wasNot Called }
    }

    @Test
    fun `fails and touches no repository when the request updates nothing`() = runBlocking<Unit> {
        // When
        val result = useCase("V-001", aRequest())

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify { vehicleRepository wasNot Called }
        coVerify { warehouseRepository wasNot Called }
    }

    // ---------- Lookup failure ----------

    @Test
    fun `propagates VehicleNotFoundException and never calls update when vehicle does not exist`() =
        runBlocking<Unit> {
            // Given
            val notFound = LogisticsException.EntityNotFoundException.VehicleNotFoundException("V-001")
            coEvery { vehicleRepository.getById("V-001") } returns Result.failure(notFound)

            // When
            val result = useCase("V-001", aRequest(maxCapacityKg = 900.0))

            // Then
            assertThat(result.exceptionOrNull()).isEqualTo(notFound)
            coVerify(exactly = 0) { vehicleRepository.update(any(), any()) }
        }

    // ---------- Merge behaviour ----------

    @Test
    fun `changes only capacity and keeps hub and cost when only capacity is provided`() = runBlocking<Unit> {
        // Given
        val saved = givenExistingVehicleAndCapturedUpdate()

        // When
        useCase("V-001", aRequest(maxCapacityKg = 900.0))

        // Then
        assertThat(saved.captured).isEqualTo(existing.copy(maxCapacityKg = 900.0))
    }

    @Test
    fun `moves the vehicle to the new hub when it exists`() = runBlocking<Unit> {
        // Given
        val saved = givenExistingVehicleAndCapturedUpdate()

        // When
        useCase("V-001", aRequest(currentHubId = "WH-002"))

        // Then
        assertThat(saved.captured.currentHub).isEqualTo(hub2)
        assertThat(saved.captured.maxCapacityKg).isEqualTo(existing.maxCapacityKg)
        assertThat(saved.captured.costPerKm).isEqualTo(existing.costPerKm)
    }

    @Test
    fun `applies every field when all fields are provided`() = runBlocking<Unit> {
        // Given
        val saved = givenExistingVehicleAndCapturedUpdate()

        // When
        useCase("V-001", aRequest(currentHubId = "WH-002", maxCapacityKg = 900.0, costPerKm = 4.5))

        // Then
        assertThat(saved.captured).isEqualTo(
            Vehicle(id = "V-001", currentHub = hub2, maxCapacityKg = 900.0, costPerKm = 4.5)
        )
    }

    // ---------- Unknown hub  ----------

    @Test
    fun `fails with WarehouseNotFoundException and never calls update when the new hub does not exist`() =
        runBlocking<Unit> {
            // Given
            coEvery { vehicleRepository.getById("V-001") } returns Result.success(existing)
            coEvery { warehouseRepository.getAll() } returns listOf(hub1, hub2)

            // When
            val result = useCase("V-001", aRequest(currentHubId = "WH-999"))

            // Then
            val error = result.exceptionOrNull()
            assertThat(error)
                .isInstanceOf(LogisticsException.EntityNotFoundException.WarehouseNotFoundException::class.java)
            assertThat((error as LogisticsException.EntityNotFoundException.WarehouseNotFoundException).id)
                .isEqualTo("WH-999")
            coVerify(exactly = 0) { vehicleRepository.update(any(), any()) }
        }

    // ---------- Repository update failure ----------

    @Test
    fun `propagates the repository failure when saving the update fails`() = runBlocking<Unit> {
        // Given
        val failure = LogisticsException.DataAccessException.NetworkUnavailableException()
        coEvery { vehicleRepository.getById("V-001") } returns Result.success(existing)
        coEvery { warehouseRepository.getAll() } returns listOf(hub1, hub2)
        coEvery { vehicleRepository.update("V-001", any()) } returns Result.failure(failure)

        // When
        val result = useCase("V-001", aRequest(maxCapacityKg = 900.0))

        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(failure)
    }

    // ---------- Helpers ----------

    private fun aRequest(
        currentHubId: String? = null,
        maxCapacityKg: Double? = null,
        costPerKm: Double? = null
    ) = VehicleUpdateRequest(currentHubId, maxCapacityKg, costPerKm)

    /** Stubs the happy-path dependencies and returns a slot holding whatever vehicle gets saved. */
    private fun givenExistingVehicleAndCapturedUpdate() = slot<Vehicle>().also { saved ->
        coEvery { vehicleRepository.getById("V-001") } returns Result.success(existing)
        coEvery { warehouseRepository.getAll() } returns listOf(hub1, hub2)
        coEvery { vehicleRepository.update("V-001", capture(saved)) } answers {
            Result.success(saved.captured)
        }
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
