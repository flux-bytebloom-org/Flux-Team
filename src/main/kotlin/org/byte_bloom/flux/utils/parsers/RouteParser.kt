package org.byte_bloom.flux.utils.parsers

import org.byte_bloom.flux.dataholders.Route
import org.byte_bloom.flux.utils.printWarningLogger


fun parseRoutes(
    lines: List<String>
):List<Route>{

    val routes =
        mutableListOf<Route>()

    for(line in lines){

        val columns =
            splitColumns(line)

        if(columns.size !=4){

            printWarningLogger(
                "Invalid route row: $line"
            )

            continue
        }

        val routeId = columns[0]
        val hubId = columns[1]

        if(routeId.isEmpty() || hubId.isEmpty()){

            printWarningLogger(
                "Missing route data: $line"
            )

            continue
        }

        val distance =
            parseDoubleOrDefault(
                columns[2],
                "distance",
                line
            )

        val delay =
            parseDoubleOrDefault(
                columns[3],
                "delay",
                line
            )

        routes.add(
            Route(
                routeId,
                hubId,
                distance,
                delay
            )
        )
    }

    return routes
}
