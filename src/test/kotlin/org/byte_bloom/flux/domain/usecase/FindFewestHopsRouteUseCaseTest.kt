package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.algorithm.routing.BreadthFirstRouter
import org.byte_bloom.flux.domain.algorithm.routing.RoutingSearchResult
import org.byte_bloom.flux.domain.testdata.createTestWarehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

private const val NODES_EXPLORED_FOR_TWO_HOP_PATH = 4
private const val NODES_EXPLORED_FOR_DIRECT_PATH = 2
private const val NODES_EXPLORED_WITHOUT_PATH = 3
private const val NODES_EXPLORED_FOR_SAME_WAREHOUSE = 1
private const val ROUTER_FAILURE_MESSAGE = "Router failure"

class FindFewestHopsRouteUseCaseTest {

    private val breadthFirstRouter = mockk<BreadthFirstRouter>()
    private val useCase = FindFewestHopsRouteUseCase(breadthFirstRouter)

    private val start = createTestWarehouse("WH-1")
    private val middle = createTestWarehouse("WH-2")
    private val destination = createTestWarehouse("WH-3")

    @Test
    fun `given connected warehouses when find fewest hops then returns router result`() {
        // Given
        val expected = RoutingSearchResult(
            path = listOf(start, middle, destination),
            nodesExplored = NODES_EXPLORED_FOR_TWO_HOP_PATH
        )
        every { breadthFirstRouter.findLeastHopPath(start, destination) } returns expected

        // When
        val result = useCase(start, destination)

        // Then
        assertThat(result).isEqualTo(expected)
        assertThat(result.path).containsExactly(start, middle, destination).inOrder()
        assertThat(result.nodesExplored).isEqualTo(NODES_EXPLORED_FOR_TWO_HOP_PATH)
    }

    @Test
    fun `given any warehouses when find fewest hops then calls router once with same warehouses`() {
        // Given
        every { breadthFirstRouter.findLeastHopPath(any(), any()) } returns RoutingSearchResult(
            path = listOf(start, destination),
            nodesExplored = NODES_EXPLORED_FOR_DIRECT_PATH
        )

        // When
        useCase(start, destination)

        // Then
        verify(exactly = 1) { breadthFirstRouter.findLeastHopPath(start, destination) }
    }

    @Test
    fun `given unreachable destination when find fewest hops then returns empty path`() {
        // Given
        val noPath = RoutingSearchResult(path = emptyList(), nodesExplored = NODES_EXPLORED_WITHOUT_PATH)
        every { breadthFirstRouter.findLeastHopPath(start, destination) } returns noPath

        // When
        val result = useCase(start, destination)

        // Then
        assertThat(result.path).isEmpty()
        assertThat(result.nodesExplored).isEqualTo(NODES_EXPLORED_WITHOUT_PATH)
    }

    @Test
    fun `given router throws when find fewest hops then exception propagates`() {
        // Given
        every { breadthFirstRouter.findLeastHopPath(start, destination) } throws
                IllegalStateException(ROUTER_FAILURE_MESSAGE)

        // When
        val exception = assertFailsWith<IllegalStateException> { useCase(start, destination) }

        // Then
        assertThat(exception).hasMessageThat().isEqualTo(ROUTER_FAILURE_MESSAGE)
    }

    @Test
    fun `given same start and destination when find fewest hops then returns single node result`() {
        // Given
        val singleNode = RoutingSearchResult(
            path = listOf(start),
            nodesExplored = NODES_EXPLORED_FOR_SAME_WAREHOUSE
        )
        every { breadthFirstRouter.findLeastHopPath(start, start) } returns singleNode

        // When
        val result = useCase(start, start)

        // Then
        assertThat(result.path).containsExactly(start)
        assertThat(result.nodesExplored).isEqualTo(NODES_EXPLORED_FOR_SAME_WAREHOUSE)
    }
}
