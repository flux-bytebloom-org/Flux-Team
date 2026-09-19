package org.byte_bloom.flux.domain.usecase.crud

import org.byte_bloom.flux.data.remote.datasource.RemotePackageDataSource
import org.byte_bloom.flux.domain.validator.PackageIdValidator
import org.byte_bloom.flux.domain.validator.ValidationResult

class DeletePackageUseCase(
    private val dataSource: RemotePackageDataSource,
    private val validator: PackageIdValidator
) {

    suspend operator fun invoke(id: String): Result<Unit> {
        val validation = validator(id)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                IllegalArgumentException(validation.errors.joinToString(", "))
            )
        }

        return runCatching {
            dataSource.delete(id)
        }
    }
}

