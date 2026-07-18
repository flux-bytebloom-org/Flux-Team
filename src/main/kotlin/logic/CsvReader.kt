package org.byte_bloom.flux.logic

import java.io.File

object CsvReader {

    fun read(filePath: String): List<String> {
        val file = File(filePath)

        if (!file.exists()) {
            println("File not found: $filePath")
            return emptyList()
        }

        return file.readLines()
            .drop(1)
            .filter { it.isNotBlank() }
    }
}