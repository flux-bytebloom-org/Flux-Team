package org.byte_bloom.flux.data.parsers

import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.utils.logWarning

private const val ROUTE_COLUMN_COUNT = 5

private const val ROUTE_ID_INDEX = 0
private const val ORIGIN_HUB_ID_INDEX = 1
private const val DESTINATION_HUB_ID_INDEX = 2
private const val DISTANCE_INDEX = 3
private const val TYPICAL_DELAY_INDEX = 4

fun parseRoutes(lines: List<String>): List<RouteRaw> {

    val routes = mutableListOf<RouteRaw>()

    for (line in lines) {

        val route = parseRouteLine(line)

        if (route != null) {
            routes.add(route)
        }
    }

    return routes
}

private fun parseRouteLine(
    rawLine: String
): RouteRaw? {

    val columns = splitColumns(rawLine)

    if (!hasValidColumnCount(
            columns,
            expectedColumnCount = ROUTE_COLUMN_COUNT,
            rawLine,
            rowType = "route"
        ) ||
        !hasRequiredRouteData(columns, rawLine)
    ) {
        return null
    }

    return createRoute(columns, rawLine)
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

        logWarning(
            "Missing route data: $line"
        )

        return false
    }

    return true
}

private fun createRoute(
    columns: List<String>,
    line: String
): RouteRaw {

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

    return RouteRaw(
        id = routeId,
        originHub = originHubId,
        destinationHub = destinationHubId,
        distanceKm = distance,
        typicalDelayMin = typicalDelay
    )
}
