package org.byte_bloom.flux.data.datasource



import java.io.File

object CsvReader {

    fun read(filePath: String): List<String> {

        val file = File(filePath)

        return file.readLines()
            .drop(1)
            .filter { line -> line.isNotBlank() }
    }
}