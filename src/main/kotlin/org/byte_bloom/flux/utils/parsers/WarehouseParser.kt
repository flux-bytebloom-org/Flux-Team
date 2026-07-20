package org.byte_bloom.flux.utils.parsers

import org.byte_bloom.flux.dataholders.Warehouse
import org.byte_bloom.flux.utils.printWarningLogger



fun parseWarehouses(
    lines: List<String>
):List<Warehouse>{

    val warehouses =
        mutableListOf<Warehouse>()

    for(line in lines){

        val columns =
            splitColumns(line)

        if(columns.size != 3){

            printWarningLogger(
                "Invalid warehouse row: $line"
            )

            continue
        }

        val id = columns[0]
        val name = columns[1]
        val zone = columns[2]

        if(id.isEmpty()){

            printWarningLogger(
                "Missing warehouse id: $line"
            )

            continue
        }

        warehouses.add(
            Warehouse(
                id,
                name,
                zone
            )
        )
    }

    return warehouses
}
