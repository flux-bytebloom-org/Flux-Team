import logic.sorters.SelectionSort
import org.byte_bloom.flux.logic.parsers.CsvParser
import org.byte_bloom.flux.logic.readers.CsvReader
import org.byte_bloom.flux.logic.parsers.PackageParser



fun main(){

    val lines = CsvReader.read("src/main/resources/packages.csv")

    val cleanLines = CsvParser.cleanLines(lines)

    val packages = PackageParser.parse(cleanLines)
    val sortedPackages = SelectionSort().sort(packages)
    println(sortedPackages)

}
