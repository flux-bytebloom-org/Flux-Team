package org.byte_bloom.flux.data.parsers

import org.byte_bloom.flux.data.dataholders.Route
import org.byte_bloom.flux.utils.printWarningLogger

private const val ROUTE_COLUMN_COUNT = 5

private const val ROUTE_ID_INDEX = 0
private const val ORIGIN_HUB_ID_INDEX = 1
private const val DESTINATION_HUB_ID_INDEX = 2
private const val DISTANCE_INDEX = 3
private const val TYPICAL_DELAY_INDEX = 4

fun parseRoutes(lines: List<String>): List<Route> {

    val routes = mutableListOf<Route>()

    for (line in lines) {

        val route = parseRouteLine(line)

        if (route != null) {
            routes.add(route)
        }
    }

    return routes
}

private fun parseRouteLine(
    line: String
): Route? {

    val columns = splitColumns(line)

    if (!hasValidColumnCount(columns, ROUTE_COLUMN_COUNT, line, "route")) {
        return null
    }

    if (!hasRequiredRouteData(columns, line)) {
        return null
    }

    return createRoute(columns, line)
}

private fun hasRequiredRouteData(
    columns: List<String>,
    line: String
): Boolean {

    val routeId = columns[ROUTE_ID_INDEX]
    val originHubId = columns[ORIGIN_HUB_ID_INDEX]
    val destinationHubId = columns[DESTINATION_HUB_ID_INDEX]

    if (
        routeId.isEmpty() ||
        originHubId.isEmpty() ||
        destinationHubId.isEmpty()
    ) {

        printWarningLogger(
            "Missing route data: $line"
        )

        return false
    }

    return true
}

private fun createRoute(
    columns: List<String>,
    line: String
): Route {

    val routeId = columns[ROUTE_ID_INDEX]
    val originHubId = columns[ORIGIN_HUB_ID_INDEX]
    val destinationHubId = columns[DESTINATION_HUB_ID_INDEX]

    val distance = parseDoubleOrDefault(
        columns[DISTANCE_INDEX],
        "distance",
        line
    )

    val typicalDelay = parseDoubleOrDefault(
        columns[TYPICAL_DELAY_INDEX],
        "delay",
        line
    )

    return Route(
        routeId = routeId,
        originHubId = originHubId,
        destinationHubId = destinationHubId,
        distanceKm = distance,
        typicalDelayMin = typicalDelay
    )
}