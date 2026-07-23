package org.byte_bloom.flux.data.parsers

import org.byte_bloom.flux.utils.printWarningLogger


fun splitColumns(line: String): List<String> {
    return line
        .trimEnd(',')
        .split(",")
        .map { it.trim() }
}

fun parseDoubleOrDefault(
    value: String,
    fieldName: String,
    line: String
): Double {

    return value.toDoubleOrNull()
        ?: run {
            printWarningLogger(
                "Invalid $fieldName in line [$line], using -1.0"
            )
            -1.0
        }
}

fun hasValidColumnCount(
    columns: List<String>,
    expectedColumnCount: Int,
    line: String,
    rowType: String
): Boolean {

    if (columns.size != expectedColumnCount) {

        printWarningLogger(
            "Invalid $rowType row: $line"

        )

        return false
    }

    return true
}
