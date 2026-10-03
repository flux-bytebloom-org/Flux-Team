package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.testdata.createTestWarehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class DecideRoutingWayUseCaseTest {

    private val findFastestPathUseCase = mockk<FindFastestPathUseCase>()
    private val findOptimalPathUseCase = mockk<FindOptimalPathUseCase>()
    private val useCase = DecideRoutingWayUseCase(findFastestPathUseCase, findOptimalPathUseCase)

    private val start = createTestWarehouse("WH-1")
    private val viaFastHub = createTestWarehouse("WH-FAST")
    private val viaShortHub = createTestWarehouse("WH-SHORT")
    private val destination = createTestWarehouse("WH-9")

    private val fastestPath = listOf(start, viaFastHub, destination)
    private val optimalPath = listOf(start, viaShortHub, destination)

    @Test
    fun `given urgent trip when decide routing then returns fastest path`() {
        // Given
        every { findFastestPathUseCase(start, destination) } returns fastestPath

        // When
        val result = useCase(start, destination, isTripUrgent = true)

        // Then
        assertThat(result).isEqualTo(fastestPath)
    }

    @Test
    fun `given urgent trip when decide routing then does not use optimal path`() {
        // Given
        every { findFastestPathUseCase(start, destination) } returns fastestPath

        // When
        useCase(start, destination, isTripUrgent = true)

        // Then
        verify(exactly = 1) { findFastestPathUseCase(start, destination) }
        verify(exactly = 0) { findOptimalPathUseCase(any(), any()) }
    }

    @Test
    fun `given non urgent trip when decide routing then returns optimal path`() {
        // Given
        every { findOptimalPathUseCase(start, destination) } returns optimalPath

        // When
        val result = useCase(start, destination, isTripUrgent = false)

        // Then
        assertThat(result).isEqualTo(optimalPath)
    }

    @Test
    fun `given non urgent trip when decide routing then does not use fastest path`() {
        // Given
        every { findOptimalPathUseCase(start, destination) } returns optimalPath

        // When
        useCase(start, destination, isTripUrgent = false)

        // Then
        verify(exactly = 1) { findOptimalPathUseCase(start, destination) }
        verify(exactly = 0) { findFastestPathUseCase(any(), any()) }
    }

    @Test
    fun `given urgent trip with no fastest path when decide routing then returns empty path`() {
        // Given
        every { findFastestPathUseCase(start, destination) } returns emptyList()

        // When
        val result = useCase(start, destination, isTripUrgent = true)

        // Then
        assertThat(result).isEmpty()
        verify(exactly = 0) { findOptimalPathUseCase(any(), any()) }
    }

    @Test
    fun `given non urgent trip with no optimal path when decide routing then returns empty path`() {
        // Given
        every { findOptimalPathUseCase(start, destination) } returns emptyList()

        // When
        val result = useCase(start, destination, isTripUrgent = false)

        // Then
        assertThat(result).isEmpty()
        verify(exactly = 0) { findFastestPathUseCase(any(), any()) }
    }

    @Test
    fun `given fastest path fails when decide routing then exception propagates`() {
        // Given
        every { findFastestPathUseCase(start, destination) } throws IllegalStateException("Routing failure")

        // When
        val exception = assertFailsWith<IllegalStateException> {
            useCase(start, destination, isTripUrgent = true)
        }

        // Then
        assertThat(exception).hasMessageThat().isEqualTo("Routing failure")
    }
}
