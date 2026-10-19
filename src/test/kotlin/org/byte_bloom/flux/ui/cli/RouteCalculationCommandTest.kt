package org.byte_bloom.flux.ui.cli

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertFailsWith
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.usecase.DecideRoutingWayUseCase
import org.junit.jupiter.api.Test

private const val ORIGIN_ID = "WH-001"
private const val ORIGIN_NAME = "Hub-001"
private const val DESTINATION_ID = "WH-002"
private const val DESTINATION_NAME = "Hub-002"
private const val UNKNOWN_WAREHOUSE_ID = "WH-999"
private const val ANY_COORDINATE = 0.0
private const val NOT_URGENT_ANSWER = "n"
private const val URGENT_ANSWER = "y"

class RouteCalculationCommandTest {

    private val warehouseRepository = mockk<WarehouseRepository>()
    private val decideRoutingWay = mockk<DecideRoutingWayUseCase>()
    private val origin = createWarehouse(ORIGIN_ID, ORIGIN_NAME, RegionalZone.CENTRAL)
    private val destination = createWarehouse(DESTINATION_ID, DESTINATION_NAME, RegionalZone.WEST)

    private fun createWarehouse(id: String, name: String, zone: RegionalZone) =
        Warehouse(id, name, zone, ANY_COORDINATE, ANY_COORDINATE)

    private fun createCommand(terminal: FakeTerminal) = RouteCalculationCommand(
        terminal, warehouseRepository, decideRoutingWay, LoadingIndicator(terminal)
    )

    @Test
    fun `given known warehouses when execute then prints the route`() = runBlocking<Unit> {
        val terminal = FakeTerminal(ORIGIN_ID, DESTINATION_ID, NOT_URGENT_ANSWER)
        coEvery { warehouseRepository.getAll() } returns listOf(origin, destination)
        every { decideRoutingWay(origin, destination, false) } returns listOf(origin, destination)

        createCommand(terminal).execute()

        assertThat(terminal.output())
            .contains("$ORIGIN_ID ($ORIGIN_NAME) -> $DESTINATION_ID ($DESTINATION_NAME)")
    }

    @Test
    fun `given no path between warehouses when execute then reports no route`() = runBlocking<Unit> {
        val terminal = FakeTerminal(ORIGIN_ID, DESTINATION_ID, URGENT_ANSWER)
        coEvery { warehouseRepository.getAll() } returns listOf(origin, destination)
        every { decideRoutingWay(origin, destination, true) } returns emptyList()

        createCommand(terminal).execute()

        assertThat(terminal.output()).contains("No route found from $ORIGIN_ID to $DESTINATION_ID")
    }

    @Test
    fun `given unknown origin when execute then throws WarehouseNotFoundException`() = runBlocking<Unit> {
        val terminal = FakeTerminal(UNKNOWN_WAREHOUSE_ID, DESTINATION_ID, NOT_URGENT_ANSWER)
        coEvery { warehouseRepository.getAll() } returns listOf(origin, destination)

        assertFailsWith<LogisticsException.EntityNotFoundException.WarehouseNotFoundException> {
            createCommand(terminal).execute()
        }
    }
}
