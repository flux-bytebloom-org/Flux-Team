package org.byte_bloom.flux.data.repositoryimplementation

import org.byte_bloom.flux.data.mapper.toDomain
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parsePackages
import org.byte_bloom.flux.data.readers.readCsv
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.repository.PackageRepository

class CsvPackageRepository(
    private val filePath: String
) : PackageRepository {

    override fun getAll(): List<Package> {
        val lines = readCsv(filePath)
        val cleanedLines = cleanLines(lines)
        return parsePackages(cleanedLines).map { it.toDomain() }
    }
}
