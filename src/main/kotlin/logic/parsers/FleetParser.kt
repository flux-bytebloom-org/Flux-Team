package org.byte_bloom.flux.logic.parsers

import org.byte_bloom.flux.dataholders.Fleet
import org.byte_bloom.flux.logic.utils.ParserLogger

object FleetParser {


    fun parse(
        lines: List<String>
    ):List<Fleet>{


        val fleet =
            mutableListOf<Fleet>()



        for(line in lines){


            val columns =
                ParserUtils.splitColumns(line)



            if(columns.size !=4){

                ParserLogger.warning(
                    "Invalid fleet row: $line"
                )

                continue
            }



            val vehicleId = columns[0]
            val hubId = columns[1]



            if(vehicleId.isEmpty() || hubId.isEmpty()){

                ParserLogger.warning(
                    "Missing fleet data: $line"
                )

                continue
            }



            val capacity =
                ParserUtils.parseDoubleOrDefault(
                    columns[2],
                    "capacity",
                    line
                )



            val cost =
                ParserUtils.parseDoubleOrDefault(
                    columns[3],
                    "cost",
                    line
                )



            fleet.add(
                Fleet(
                    vehicleId,
                    hubId,
                    capacity,
                    cost
                )
            )
        }


        return fleet
    }
}