package org.byte_bloom.flux.data.repositoryimplementation


import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parsePackages
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.repository.PackageRepository

class CsvPackageRepository(
    private val filePath: String
) : PackageRepository {

    override fun getAll(): List<PackageRaw> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parsePackages(cleanedLines)
    }
}
