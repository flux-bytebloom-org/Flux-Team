package org.byte_bloom.flux.utils.parsers

import org.byte_bloom.flux.dataholders.Route
import org.byte_bloom.flux.utils.printWarningLogger


private const val ROUTE_COLUMN_NUMBER = 4

private const val ROUTE_COLUMN_INDEX_ROUTE_ID = 0
private const val ROUTE_COLUMN_INDEX_HUB_ID = 1
private const val ROUTE_COLUMN_INDEX_DISTANCE = 2
private const val ROUTE_COLUMN_INDEX_DELAY = 3

fun parseRoutes(lines: List<String>): List<Route> {
    val routes = mutableListOf<Route>()
    for (line in lines) {
        val columns = splitColumns(line)
        if (columns.size != ROUTE_COLUMN_NUMBER) { printWarningLogger("Invalid route row: $line"); continue }

        val routeId = columns[ROUTE_COLUMN_INDEX_ROUTE_ID]
        val hubId = columns[ROUTE_COLUMN_INDEX_HUB_ID]
        if (routeId.isEmpty() || hubId.isEmpty()) { printWarningLogger("Missing route data: $line"); continue }

        val distance = parseDoubleOrDefault(columns[ROUTE_COLUMN_INDEX_DISTANCE], "distance", line)
        val delay = parseDoubleOrDefault(columns[ROUTE_COLUMN_INDEX_DELAY], "delay", line)
        routes.add(Route(routeId, hubId, distance, delay))
    }
    return routes
}
