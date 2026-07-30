package org.byte_bloom.flux.data.readers

import java.io.File

fun readCsv(filePath: String): List<String> {

    val file = File(filePath)

    if (!file.exists()) {
        println("File not found: $filePath")
        return emptyList()
    }

    return file.readLines()
}
