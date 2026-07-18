package org.byte_bloom.flux

import org.byte_bloom.flux.logic.parsers.CsvParser
import org.byte_bloom.flux.logic.readers.CsvReader
import org.byte_bloom.flux.logic.parsers.PackageParser


fun main(){

    val lines = CsvReader.read("packages.csv")

    val cleanLines = CsvParser.cleanLines(lines)

    val packages = PackageParser.parse(cleanLines)

    println(packages)

}