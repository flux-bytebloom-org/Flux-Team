package org.byte_bloom.flux

import org.byte_bloom.flux.logic.parsers.CsvParser
import org.byte_bloom.flux.logic.readers.CsvReader
import org.byte_bloom.flux.logic.parsers.PackageParser
import org.byte_bloom.flux.logic.sorters.SelectionSort


fun main(){

    val lines = CsvReader.read("src/org.byte_bloom.flux.main/resources/packages.csv")

    val cleanLines = CsvParser.cleanLines(lines)

    val packages = PackageParser.parse(cleanLines)
    val sortedPackages = SelectionSort().sort(packages)
    println(sortedPackages)

}
