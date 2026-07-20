package org.byte_bloom.flux.utils.parsers

import org.byte_bloom.flux.dataholders.Package
import org.byte_bloom.flux.dataholders.Priority
import org.byte_bloom.flux.utils.printWarningLogger



fun parsePackages(
    lines: List<String>
): List<Package> {

    val packages = mutableListOf<Package>()

    for(line in lines){

        val columns = splitColumns(line)

        if(columns.size != 4){

            printWarningLogger(
                "Invalid column count: $line"
            )

            continue
        }

        val id = columns[0]
        val weightValue = columns[1]
        val destination = columns[2]
        val priorityValue = columns[3]

        if(id.isEmpty() || destination.isEmpty()){

            printWarningLogger(
                "Missing required fields: $line"
            )

            continue
        }

        val weight =
            parseDoubleOrDefault(
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
