package org.byte_bloom.flux.logic.parsers

import org.byte_bloom.flux.dataholders.Warehouse
import org.byte_bloom.flux.logic.utils.ParserLogger


object WarehouseParser {


    fun parse(
        lines: List<String>
    ):List<Warehouse>{


        val warehouses =
            mutableListOf<Warehouse>()


        for(line in lines){


            val columns =
                ParserUtils.splitColumns(line)



            if(columns.size != 3){

                ParserLogger.warning(
                    "Invalid warehouse row: $line"
                )

                continue
            }



            val id = columns[0]
            val name = columns[1]
            val zone = columns[2]



            if(id.isEmpty()){

                ParserLogger.warning(
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
}