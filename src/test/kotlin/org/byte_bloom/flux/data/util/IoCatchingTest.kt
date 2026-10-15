package org.byte_bloom.flux.data.util

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class IoCatchingTest {

    @Test
    fun `given block returns value when ioCatching then returns success`() = runTest {
        // When
        val result = ioCatching { 42 }

        // Then
        assertThat(result.getOrNull()).isEqualTo(42)
    }

    @Test
    fun `given block throws exception when ioCatching then returns failure`() = runTest {
        // When
        val result = ioCatching<Unit> { error("boom") }

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `given block throws CancellationException when ioCatching then rethrows it`() = runTest {
        // When / Then
        assertFailsWith<CancellationException> {
            ioCatching<Unit> { throw CancellationException("cancelled") }
        }
    }
}