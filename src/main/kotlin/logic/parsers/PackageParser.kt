package org.byte_bloom.flux.logic.parsers

import org.byte_bloom.flux.dataholders.Package
import org.byte_bloom.flux.dataholders.Priority
import org.byte_bloom.flux.logic.utils.ParserLogger

object PackageParser {


    fun parse(
        lines: List<String>
    ): List<Package> {


        val packages = mutableListOf<Package>()


        for(line in lines){


            val columns =
                ParserUtils.splitColumns(line)



            if(columns.size != 4){

                ParserLogger.warning(
                    "Invalid column count: $line"
                )

                continue
            }



            val id = columns[0]
            val weightValue = columns[1]
            val destination = columns[2]
            val priorityValue = columns[3]



            if(id.isEmpty() || destination.isEmpty()){

                ParserLogger.warning(
                    "Missing required fields: $line"
                )

                continue
            }



            val weight =
                ParserUtils.parseDoubleOrDefault(
                    weightValue,
                    "weight",
                    line
                )


            val priority =
                parsePriority(priorityValue)



            packages.add(
                Package(
                    id,
                    weight,
                    destination,
                    priority
                )
            )
        }


        return packages
    }



    private fun parsePriority(
        value:String
    ): Priority {


        return when(value.uppercase()){

            "URGENT" ->
                Priority.URGENT


            "STANDARD" ->
                Priority.STANDARD


            else ->
                Priority.LOW
        }
    }
}