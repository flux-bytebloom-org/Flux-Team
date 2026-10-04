package org.byte_bloom.flux.domain.usecase.crud.packages

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.byte_bloom.flux.domain.usecase.pakage.GetPackageByIdUseCase
import org.junit.jupiter.api.Test

class GetPackageByIdUseCaseTest {

    private val repository: PackageRepository = mockk()
    private val useCase = org.byte_bloom.flux.domain.usecase.pakage.GetPackageByIdUseCase(repository)

    @Test
    fun `given existing id when get then returns the package`() = runTest {
        // Given
        val pkg = createTestPackage(id = "PKG-1")
        coEvery { repository.getById("PKG-1") } returns Result.success(pkg)

        // When
        val result = useCase("PKG-1")

        // Then
        assertThat(result.getOrNull()).isEqualTo(pkg)
    }

    @Test
    fun `given id with wrong prefix when get then fails without calling repository`() = runTest {
        // When
        val result = useCase("WH-1")

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.getById(any()) }
    }

    @Test
    fun `given unknown id when get then propagates PackageNotFoundException`() = runTest {
        // Given
        val error = LogisticsException.EntityNotFoundException.PackageNotFoundException("PKG-404")
        coEvery { repository.getById("PKG-404") } returns Result.failure(error)

        // When
        val result = useCase("PKG-404")

        // Then
        assertThat(result.exceptionOrNull()).isSameInstanceAs(error)
    }
}