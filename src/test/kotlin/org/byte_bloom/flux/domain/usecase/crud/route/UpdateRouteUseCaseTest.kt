package org.byte_bloom.flux.domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.request.RouteUpdateRequest
import org.byte_bloom.flux.domain.testdata.createRouteTestWarehouse
import org.byte_bloom.flux.domain.testdata.createTestRoute
import org.junit.jupiter.api.Test
import org.byte_bloom.flux.domain.usecase.route.UpdateRouteUseCase

class UpdateRouteUseCaseTest {

    private val repository = mockk<RouteRepository>()
    private val warehouseRepository = mockk<WarehouseRepository>()
    private val useCase = UpdateRouteUseCase(repository, warehouseRepository)

    private val existing = createTestRoute(id = "RT-1", distanceKm = 100.0, typicalDelayMin = 10.0)
    private val newHub = createRouteTestWarehouse("WH-9")

    private fun request(
        originHubId: String? = null,
        destinationHubId: String? = null,
        distanceKm: Double? = null,
        typicalDelayMin: Double? = null
    ) = RouteUpdateRequest(originHubId, destinationHubId, distanceKm, typicalDelayMin)

    private fun stubHappyPath() {
        coEvery { repository.getById("RT-1") } returns Result.success(existing)
        coEvery { warehouseRepository.getAll() } returns listOf(existing.originHub, existing.destinationHub, newHub)
        coEvery { repository.update("RT-1", any()) } answers { Result.success(secondArg<Route>()) }
    }

    @Test
    fun `given new distance when update then only distance changes`() = runBlocking<Unit> {
        // Given
        stubHappyPath()

        // When
        val result = useCase("RT-1", request(distanceKm = 80.0))

        // Then
        val updated = result.getOrThrow()
        assertThat(updated.distanceKm).isEqualTo(80.0)
        assertThat(updated.typicalDelayMin).isEqualTo(existing.typicalDelayMin)
        assertThat(updated.originHub).isEqualTo(existing.originHub)
        assertThat(updated.destinationHub).isEqualTo(existing.destinationHub)
    }

    @Test
    fun `given new origin hub id when update then origin hub is replaced`() = runBlocking<Unit> {
        // Given
        stubHappyPath()

        // When
        val result = useCase("RT-1", request(originHubId = "WH-9"))

        // Then
        val updated = result.getOrThrow()
        assertThat(updated.originHub).isEqualTo(newHub)
        assertThat(updated.destinationHub).isEqualTo(existing.destinationHub)
    }

    @Test
    fun `given unknown hub id when update then keeps existing hub`() = runBlocking<Unit> {
        // Given
        stubHappyPath()

        // When
        val result = useCase("RT-1", request(destinationHubId = "WH-404"))

        // Then
        assertThat(result.getOrThrow().destinationHub).isEqualTo(existing.destinationHub)
    }

    @Test
    fun `given valid request when update then sends merged route to repository`() = runBlocking<Unit> {
        // Given
        stubHappyPath()
        val expected = existing.copy(distanceKm = 70.0, typicalDelayMin = 5.0)

        // When
        useCase("RT-1", request(distanceKm = 70.0, typicalDelayMin = 5.0))

        // Then
        coVerify(exactly = 1) { repository.update("RT-1", expected) }
    }

    @Test
    fun `given invalid id when update then returns EntityValidationException`() = runBlocking<Unit> {
        // Given
        val invalidId = "WH-1"

        // When
        val result = useCase(invalidId, request(distanceKm = 80.0))

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.getById(any()) }
        coVerify(exactly = 0) { repository.update(any(), any()) }
    }

    @Test
    fun `given empty request when update then returns EntityValidationException`() = runBlocking<Unit> {
        // Given
        val emptyRequest = request()

        // When
        val result = useCase("RT-1", emptyRequest)

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.getById(any()) }
    }

    @Test
    fun `given negative delay when update then returns EntityValidationException`() = runBlocking<Unit> {
        // Given
        val invalidRequest = request(typicalDelayMin = -1.0)

        // When
        val result = useCase("RT-1", invalidRequest)

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.update(any(), any()) }
    }

    @Test
    fun `given missing route when update then returns failure without updating`() = runBlocking<Unit> {
        // Given
        val notFound = LogisticsException.EntityNotFoundException.RouteNotFoundException("RT-1")
        coEvery { repository.getById("RT-1") } returns Result.failure(notFound)

        // When
        val result = useCase("RT-1", request(distanceKm = 80.0))

        // Then
        assertThat(result.exceptionOrNull()).isSameInstanceAs(notFound)
        coVerify(exactly = 0) { repository.update(any(), any()) }
    }

    @Test
    fun `given valid request when repository update fails then returns repository failure`() = runBlocking<Unit> {
        // Given
        val conflict = LogisticsException.DataAccessException.DatabaseConflictException("Update conflict")
        coEvery { repository.getById("RT-1") } returns Result.success(existing)
        coEvery { warehouseRepository.getAll() } returns emptyList()
        coEvery { repository.update("RT-1", any()) } returns Result.failure(conflict)

        // When
        val result = useCase("RT-1", request(distanceKm = 80.0))

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(conflict)
    }
}
