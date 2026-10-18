package org.byte_bloom.flux.ui.cli

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.usecase.DecideRoutingWayUseCase

private const val LOADING_MESSAGE = "Calculating route..."
private const val ROUTE_SEPARATOR = " -> "

class RouteCalculationCommand(
    private val terminal: Terminal,
    private val warehouseRepository: WarehouseRepository,
    private val decideRoutingWay: DecideRoutingWayUseCase,
    private val loadingIndicator: LoadingIndicator
) : CliCommand {

    override val label = "Calculate a route between two warehouses"

    override suspend fun execute() {
        val originId = terminal.readTrimmed("Origin warehouse id: ")
        val destinationId = terminal.readTrimmed("Destination warehouse id: ")
        val isTripUrgent = terminal.readYesNo("Is the trip urgent? (y/n): ")

        val route = loadingIndicator.whileLoading(LOADING_MESSAGE) {
            calculateRoute(originId, destinationId, isTripUrgent)
        }
        terminal.writeLine(describe(route, originId, destinationId))
    }

    private suspend fun calculateRoute(
        originId: String,
        destinationId: String,
        isTripUrgent: Boolean
    ): List<Warehouse> {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        val origin = warehousesById.findOrThrow(originId)
        val destination = warehousesById.findOrThrow(destinationId)
        return withContext(Dispatchers.Default) {
            decideRoutingWay(origin, destination, isTripUrgent)
        }
    }

    private fun describe(route: List<Warehouse>, originId: String, destinationId: String): String =
        if (route.isEmpty()) {
            "No route found from $originId to $destinationId"
        } else {
            "Route: " + route.joinToString(ROUTE_SEPARATOR) { "${it.id} (${it.name})" }
        }

    private fun Map<String, Warehouse>.findOrThrow(id: String): Warehouse =
        this[id] ?: throw LogisticsException.EntityNotFoundException.WarehouseNotFoundException(id)
}
