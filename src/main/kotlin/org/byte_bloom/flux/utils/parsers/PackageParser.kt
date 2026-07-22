package org.byte_bloom.flux.utils.parsers

import org.byte_bloom.flux.dataholders.Package
import org.byte_bloom.flux.dataholders.Priority
import org.byte_bloom.flux.utils.printWarningLogger

private const val PACKAGE_COLUMN_NUMBER= 4

private const val COLUMN_INDEX_ID = 0
private const val COLUMN_INDEX_WEIGHT = 1
private const val COLUMN_INDEX_DESTINATION = 2
private const val COLUMN_INDEX_PRIORITY = 3

fun parsePackages(lines: List<String>): List<Package> {
    val packages = mutableListOf<Package>()

    for (line in lines) {
        val columns = splitColumns(line)

        if (columns.size != PACKAGE_COLUMN_NUMBER) {
            printWarningLogger("Invalid column count: $line")
            continue
        }

        val id = columns[COLUMN_INDEX_ID]
        val weightValue = columns[COLUMN_INDEX_WEIGHT]
        val destination = columns[COLUMN_INDEX_DESTINATION]
        val priorityValue = columns[COLUMN_INDEX_PRIORITY]

        if (id.isEmpty() || destination.isEmpty()) {
            printWarningLogger("Missing required fields: $line")
            continue
        }
        val weight = parseDoubleOrDefault(weightValue, "weight", line)
        val priority = parsePriority(priorityValue)
        packages.add(Package(id, weight, destination, priority))
    }

    return packages
}

private fun parsePriority(value: String): Priority {
    return when (value.uppercase()) {
        "URGENT" -> Priority.URGENT
        "STANDARD" -> Priority.STANDARD
        else -> Priority.LOW
    }
}