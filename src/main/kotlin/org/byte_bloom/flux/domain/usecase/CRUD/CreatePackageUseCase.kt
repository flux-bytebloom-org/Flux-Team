
package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.data.remote.datasource.RemotePackageDataSource
import org.byte_bloom.flux.data.remote.dto.toDomain
import org.byte_bloom.flux.data.remote.dto.toRequestDto
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.validator.PackageCreateValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

class CreatePackageUseCase(
    private val dataSource: RemotePackageDataSource,
    private val validator: PackageCreateValidator
) {

    suspend operator fun invoke(pkg: Package): Result<Package> {
        val validation = validator(pkg)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                IllegalArgumentException(validation.errors.joinToString(", "))
            )
        }

        return runCatching {
            val requestDto = pkg.toRequestDto()
            val responseDto = dataSource.create(requestDto)
            responseDto.toDomain()
        }
    }
}
