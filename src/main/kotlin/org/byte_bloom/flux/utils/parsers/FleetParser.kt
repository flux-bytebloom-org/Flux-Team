package org.byte_bloom.flux.utils.parsers

import org.byte_bloom.flux.dataholders.Vehicle
import org.byte_bloom.flux.utils.printWarningLogger



fun parseVehicles(
    lines: List<String>
):List<Vehicle>{

    val fleet = mutableListOf<Vehicle>()

    for(line in lines){

        val columns = splitColumns(line)

        if(columns.size !=4){
            printWarningLogger(
                "Invalid fleet row: $line"
            )

            continue
        }

        val vehicleId = columns[0]
        val hubId = columns[1]

        if(vehicleId.isEmpty() || hubId.isEmpty()){

            printWarningLogger(
                "Missing fleet data: $line"
            )

            continue
        }

        val capacity =parseDoubleOrDefault(
                columns[2],
                "capacity",
                line
            )

        val cost =
            parseDoubleOrDefault(
                columns[3],
                "cost",
                line
            )

        fleet.add(
            Vehicle(
                vehicleId,
                hubId,
                capacity,
                cost
            )
        )
    }

    return fleet
}
