package org.byte_bloom.flux.domain.usecase.crud.packages

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.byte_bloom.flux.domain.usecase.pakage.CreatePackageUseCase
import org.byte_bloom.flux.domain.validator.packagevalidations.PackageCreateValidator
import org.junit.jupiter.api.Test

class CreatePackageUseCaseTest {

    private val repository: PackageRepository = mockk()
    private val useCase = org.byte_bloom.flux.domain.usecase.pakage.CreatePackageUseCase(
        repository,
        PackageCreateValidator()
    )

    @Test
    fun `given valid package when create then returns success from repository`() = runTest {
        // Given
        val pkg = createTestPackage()
        coEvery { repository.create(pkg) } returns Result.success(pkg)

        // When
        val result = useCase(pkg)

        // Then
        assertThat(result.getOrNull()).isEqualTo(pkg)
        coVerify(exactly = 1) { repository.create(pkg) }
    }

    @Test
    fun `given invalid weight when create then fails with validation exception and skips repository`() = runTest {
        // Given
        val pkg = createTestPackage(weight = -1.0)

        // When
        val result = useCase(pkg)

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.create(any()) }
    }

    @Test
    fun `given repository conflict when create then propagates the same exception`() = runTest {
        // Given
        val pkg = createTestPackage()
        val error = LogisticsException.DataAccessException.DatabaseConflictException("duplicate id")
        coEvery { repository.create(pkg) } returns Result.failure(error)

        // When
        val result = useCase(pkg)

        // Then
        assertThat(result.exceptionOrNull()).isSameInstanceAs(error)
    }
}