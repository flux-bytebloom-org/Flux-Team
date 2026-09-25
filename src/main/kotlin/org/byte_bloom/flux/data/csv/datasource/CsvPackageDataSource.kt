package org.byte_bloom.flux.data.csv.datasource

import org.byte_bloom.flux.data.csv.dataholders.PackageRaw
import org.byte_bloom.flux.data.csv.parsers.cleanLines
import org.byte_bloom.flux.data.csv.parsers.parsePackages
import org.byte_bloom.flux.data.csv.readers.readCsv

class CsvPackageDataSource(
    private val filePath: String
) : PackageDataSource {
    override fun getAll(): List<PackageRaw> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parsePackages(cleanedLines)
    }

    override fun updateOriginHub(packageId: String, hubId: String) {
        println("[[temp]] (1/2) Updating package $packageId's originHub -> $hubId")
        println("[[temp]] (2/2) Placing package $packageId in hub $hubId's cargo queue")
        //TODO(sort hub's Queue after insertion )
    }

    override fun removeFromHub(packageId: String, hubId: String) {
        println("[[temp]] (1/2) Removing package $packageId's hub association")
        println("[[temp]] (2/2) Removing package $packageId from hub $hubId's cargo queue")
        //TODO("Not yet implemented")
    }

    override fun updateDestination(packageId: String, hubId: String) {
        println("[[temp]] Updating package $packageId destination to $hubId in CSV repository (not implemented YET)")
        TODO("Not yet implemented")
    }
}
