package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.algorithm.routing.DijkstraRouter
import org.byte_bloom.flux.domain.algorithm.routing.RoutingCriterion
import org.byte_bloom.flux.domain.testdata.createTestWarehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class FindOptimalPathUseCaseTest {

    private val dijkstraRouter = mockk<DijkstraRouter>()
    private val useCase = FindOptimalPathUseCase(dijkstraRouter)

    private val start = createTestWarehouse("WH-1")
    private val middle = createTestWarehouse("WH-2")
    private val destination = createTestWarehouse("WH-3")

    @Test
    fun `given connected warehouses when find optimal path then returns router path`() {
        // Given
        val expectedPath = listOf(start, middle, destination)
        every { dijkstraRouter.findShortestPath(start, destination, RoutingCriterion.DISTANCE) } returns expectedPath

        // When
        val result = useCase(start, destination)

        // Then
        assertThat(result).containsExactly(start, middle, destination).inOrder()
    }

    @Test
    fun `given any warehouses when find optimal path then uses DISTANCE criterion`() {
        // Given
        every { dijkstraRouter.findShortestPath(any(), any(), any()) } returns listOf(start, destination)

        // When
        useCase(start, destination)

        // Then
        verify(exactly = 1) { dijkstraRouter.findShortestPath(start, destination, RoutingCriterion.DISTANCE) }
        verify(exactly = 0) { dijkstraRouter.findShortestPath(any(), any(), RoutingCriterion.TIME_DELAY) }
    }

    @Test
    fun `given unreachable destination when find optimal path then returns empty path`() {
        // Given
        every { dijkstraRouter.findShortestPath(start, destination, RoutingCriterion.DISTANCE) } returns emptyList()

        // When
        val result = useCase(start, destination)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `given router throws when find optimal path then exception propagates`() {
        // Given
        every { dijkstraRouter.findShortestPath(start, destination, RoutingCriterion.DISTANCE) } throws
                IllegalStateException("Router failure")

        // When
        val exception = assertFailsWith<IllegalStateException> { useCase(start, destination) }

        // Then
        assertThat(exception).hasMessageThat().isEqualTo("Router failure")
    }

    @Test
    fun `given same start and destination when find optimal path then returns single warehouse`() {
        // Given
        every { dijkstraRouter.findShortestPath(start, start, RoutingCriterion.DISTANCE) } returns listOf(start)

        // When
        val result = useCase(start, start)

        // Then
        assertThat(result).containsExactly(start)
    }
}
