package org.byte_bloom.flux.domain.usecase

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.builder.ShadowWarehouseGraphBuilder
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PenalizeBottleneckRoutesUseCaseTest {

    private val builder = mockk<ShadowWarehouseGraphBuilder>()
    private val useCase = PenalizeBottleneckRoutesUseCase(builder)

    private fun warehouse(id: String) =
        Warehouse(
            id = id,
            name = id,
            regionalZone = RegionalZone.CENTRAL,
            latitude = 0.0,
            longitude = 0.0
        )

    @Test
    fun `should return result from shadow graph builder`() {
        // Given
        val bottleneck = warehouse("W2")
        val warehouses = listOf(
            warehouse("W1"),
            bottleneck,
            warehouse("W3")
        )

        val expected = mapOf(
            "W1" to warehouses[0],
            "W2" to warehouses[1],
            "W3" to warehouses[2]
        )

        every {
            builder.build(
                allWarehouses = warehouses,
                bottleneckWarehouse = bottleneck,
                penaltyFactor = 1.5
            )
        } returns expected

        // When
        val result = useCase(
            bottleneckWarehouse = bottleneck,
            allWarehouses = warehouses,
            penaltyFactor = 1.5
        )

        // Then
        assertEquals(expected, result)

        verify(exactly = 1) {
            builder.build(
                allWarehouses = warehouses,
                bottleneckWarehouse = bottleneck,
                penaltyFactor = 1.5
            )
        }
    }

    @Test
    fun `should pass default penalty factor to builder`() {
        // Given
        val bottleneck = warehouse("W1")
        val warehouses = listOf(bottleneck)

        every {
            builder.build(
                allWarehouses = warehouses,
                bottleneckWarehouse = bottleneck,
                penaltyFactor = 1.3
            )
        } returns emptyMap()

        // When
        val result = useCase(
            bottleneckWarehouse = bottleneck,
            allWarehouses = warehouses
        )

        // Then
        assertTrue(result.isEmpty())

        verify(exactly = 1) {
            builder.build(
                allWarehouses = warehouses,
                bottleneckWarehouse = bottleneck,
                penaltyFactor = 1.3
            )
        }
    }

    @Test
    fun `should work with penalty factor one`() {
        // Given
        val bottleneck = warehouse("W1")
        val warehouses = listOf(bottleneck)

        every {
            builder.build(
                allWarehouses = warehouses,
                bottleneckWarehouse = bottleneck,
                penaltyFactor = 1.0
            )
        } returns emptyMap()

        // When
        val result = useCase(
            bottleneckWarehouse = bottleneck,
            allWarehouses = warehouses,
            penaltyFactor = 1.0
        )

        // Then
        assertTrue(result.isEmpty())
    }
}