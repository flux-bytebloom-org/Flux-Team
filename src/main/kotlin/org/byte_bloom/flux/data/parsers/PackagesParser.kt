package org.byte_bloom.flux.data.parsers

import org.byte_bloom.flux.data.dataholders.Package
import org.byte_bloom.flux.data.dataholders.Priority
import org.byte_bloom.flux.utils.printWarningLogger

private const val PACKAGE_COLUMN_COUNT = 4

private const val PACKAGE_ID_INDEX = 0
private const val PACKAGE_WEIGHT_INDEX = 1
private const val PACKAGE_DESTINATION_INDEX = 2
private const val PACKAGE_PRIORITY_INDEX = 3

fun parsePackages(lines: List<String>): List<Package> {

    val packages = mutableListOf<Package>()

    for (line in lines) {

        val packageData = parsePackageLine(line)

        if (packageData != null) {
            packages.add(packageData)
        }
    }

    return packages
}

private fun parsePackageLine(
    line: String
): Package? {

    val columns = splitColumns(line)

    if (!hasValidColumnCount(columns, PACKAGE_COLUMN_COUNT, line, "package")) {
        return null
    }

    if (!hasRequiredPackageData(columns, line)) {
        return null
    }

    return createPackage(columns, line)
}

private fun hasRequiredPackageData(
    columns: List<String>,
    line: String
): Boolean {

    val packageId = columns[PACKAGE_ID_INDEX]
    val destinationHubId = columns[PACKAGE_DESTINATION_INDEX]

    if (packageId.isEmpty() || destinationHubId.isEmpty()) {

        printWarningLogger(
            "Missing required package data: $line"
        )

        return false
    }

    return true
}

private fun createPackage(
    columns: List<String>,
    line: String
): Package {

    val packageId = columns[PACKAGE_ID_INDEX]

    val weight = parseDoubleOrDefault(
        columns[PACKAGE_WEIGHT_INDEX],
        "weight",
        line
    )

    val destinationHubId = columns[PACKAGE_DESTINATION_INDEX]

    val priority = parsePriority(
        columns[PACKAGE_PRIORITY_INDEX]
    )

    return Package(
        packageId,
        weight,
        destinationHubId,
        priority
    )
}

private fun parsePriority(value: String): Priority {

    return when (value.uppercase()) {
        "URGENT" -> Priority.URGENT
        "STANDARD" -> Priority.STANDARD
        else -> Priority.LOW
    }
}