package org.byte_bloom.flux.utils.parsers

import org.byte_bloom.flux.utils.ParserLogger

object ParserUtils {

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
                ParserLogger.warning(
                    "Invalid $fieldName in line [$line], using -1.0"
                )
                -1.0
            }
    }
}