package org.byte_bloom.flux.data.parsers

private const val INVALID_NUMERIC_FIELD_DEFAULT = -1.0
private const val HEADER_ROW_COUNT = 1

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
            logWarning(
                "Invalid $fieldName in line [$line], using $INVALID_NUMERIC_FIELD_DEFAULT"
            )
            INVALID_NUMERIC_FIELD_DEFAULT
        }
}

fun hasValidColumnCount(
    columns: List<String>,
    expectedColumnCount: Int,
    line: String,
    rowType: String
): Boolean {

    if (columns.size != expectedColumnCount) {

        logWarning(
            "Invalid $rowType row: $line"

        )

        return false
    }

    return true
}

fun cleanLines(
    rawLines: List<String>
): List<String> {

    return rawLines
        .drop(HEADER_ROW_COUNT)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}

fun logWarning(message: String) {
    println("WARNING: $message")
}
