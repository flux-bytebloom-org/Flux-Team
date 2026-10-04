package org.byte_bloom.flux.domain.usecase.crud.packages

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.usecase.pakage.DeletePackageUseCase
import org.junit.jupiter.api.Test

class DeletePackageUseCaseTest {

    private val repository: PackageRepository = mockk()
    private val useCase = DeletePackageUseCase(repository)

    @Test
    fun `given valid id when delete then returns success`() = runTest {
        // Given
        coEvery { repository.delete("PKG-1") } returns Result.success(Unit)

        // When
        val result = useCase("PKG-1")

        // Then
        assertThat(result.isSuccess).isTrue()
        coVerify(exactly = 1) { repository.delete("PKG-1") }
    }

    @Test
    fun `given id with wrong prefix when delete then fails without calling repository`() = runTest {
        // When
        val result = useCase("V-1")

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify(exactly = 0) { repository.delete(any()) }
    }

    @Test
    fun `given repository failure when delete then propagates the same exception`() = runTest {
        // Given
        val error = LogisticsException.EntityNotFoundException.PackageNotFoundException("PKG-404")
        coEvery { repository.delete("PKG-404") } returns Result.failure(error)

        // When
        val result = useCase("PKG-404")

        // Then
        assertThat(result.exceptionOrNull()).isSameInstanceAs(error)
    }
}
