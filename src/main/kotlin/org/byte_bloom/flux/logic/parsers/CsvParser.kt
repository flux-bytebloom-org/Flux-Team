package org.byte_bloom.flux.logic.parsers

object CsvParser {

    fun cleanLines(
        rawLines: List<String>
    ): List<String> {

        return rawLines
            .drop(1)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}