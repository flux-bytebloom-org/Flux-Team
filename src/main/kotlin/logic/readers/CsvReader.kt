package org.byte_bloom.flux.logic.readers

import java.io.File

object CsvReader {

    fun read(filePath: String): List<String> {

        val file = File(filePath)

        if (!file.exists()) {
            println("File not found: $filePath")
            return emptyList()
        }

        return file.readLines()
    }
}