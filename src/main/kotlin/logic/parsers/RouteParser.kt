package org.byte_bloom.flux.logic.parsers

import org.byte_bloom.flux.dataholders.Route
import org.byte_bloom.flux.logic.utils.ParserLogger

object RouteParser {


    fun parse(
        lines: List<String>
    ):List<Route>{


        val routes =
            mutableListOf<Route>()



        for(line in lines){


            val columns =
                ParserUtils.splitColumns(line)



            if(columns.size !=4){

                ParserLogger.warning(
                    "Invalid route row: $line"
                )

                continue
            }



            val routeId = columns[0]
            val hubId = columns[1]



            if(routeId.isEmpty() || hubId.isEmpty()){

                ParserLogger.warning(
                    "Missing route data: $line"
                )

                continue
            }



            val distance =
                ParserUtils.parseDoubleOrDefault(
                    columns[2],
                    "distance",
                    line
                )


            val delay =
                ParserUtils.parseDoubleOrDefault(
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
}