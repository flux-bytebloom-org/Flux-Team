package org.byte_bloom.flux.data.parsers

import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.dataholders.Priority
import org.byte_bloom.flux.utils.logWarning

private const val PACKAGE_COLUMN_COUNT = 5

private const val PACKAGE_ID_INDEX = 0
private const val PACKAGE_WEIGHT_INDEX = 1
private const val PACKAGE_ORIGIN_INDEX = 2
private const val PACKAGE_DESTINATION_INDEX = 3
private const val PACKAGE_PRIORITY_INDEX = 4

fun parsePackages(lines: List<String>): List<PackageRaw> {

    val packages = mutableListOf<PackageRaw>()

    for (line in lines) {

        val packageData = parsePackageLine(line)

        if (packageData != null) {
            packages.add(packageData)
        }
    }

    return packages
}

private fun parsePackageLine(
    rawLine: String
): PackageRaw? {

    val columns = splitColumns(rawLine)

    if (!hasValidColumnCount(
            columns,
            expectedColumnCount = PACKAGE_COLUMN_COUNT,
            rawLine,
            rowType = "package"
        ) ||
        !hasRequiredPackageData(columns, rawLine)
    ) {
        return null
    }

    return createPackage(columns, rawLine)
}

private fun hasRequiredPackageData(
    columns: List<String>,
    line: String
): Boolean {

    val packageId = columns[PACKAGE_ID_INDEX]
    val originHubId = columns[PACKAGE_ORIGIN_INDEX]
    val destinationHubId = columns[PACKAGE_DESTINATION_INDEX]

    if (
        packageId.isEmpty() ||
        originHubId.isEmpty() ||
        destinationHubId.isEmpty()
    ) {
        logWarning(
            "Missing required package data: $line"
        )

        return false
    }

    return true
}

private fun createPackage(
    columns: List<String>,
    line: String
): PackageRaw {

    val packageId = columns[PACKAGE_ID_INDEX]

    val weight = parseDoubleOrDefault(
        columns[PACKAGE_WEIGHT_INDEX],
        "weight",
        line
    )

    val originHubId = columns[PACKAGE_ORIGIN_INDEX]

    val destinationHubId = columns[PACKAGE_DESTINATION_INDEX]

    val priority = parsePriority(
        columns[PACKAGE_PRIORITY_INDEX]
    )

    return PackageRaw(
        id = packageId,
        weight = weight,
        originHubId = originHubId,
        destinationHubId = destinationHubId,
        priority = priority
    )
}

private fun parsePriority(value: String): Priority {

    return when (value.uppercase()) {
        "URGENT" -> Priority.URGENT
        "STANDARD" -> Priority.STANDARD
        else -> Priority.LOW
    }
}
