package org.byte_bloom.flux.domain.algorithm.knn

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.model.RegionalZone
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class NearestNeighborsTest {

    // One-dimensional points so the distances are easy to read:
    // distance from the query [0.0] to each record = |x|
    private val training = listOf(
        LabeledPoint(listOf(5.0), RegionalZone.NORTH),    // distance 5
        LabeledPoint(listOf(1.0), RegionalZone.SOUTH),    // distance 1
        LabeledPoint(listOf(3.0), RegionalZone.EAST),     // distance 3
        LabeledPoint(listOf(2.0), RegionalZone.WEST),     // distance 2
        LabeledPoint(listOf(4.0), RegionalZone.CENTRAL)   // distance 4
    )
    //new package that we need to know his sector
    private val query = listOf(0.0)

    @Test
    fun `given k smaller than training size when finding neighbors then returns k nearest in order`() {
        // When
        val result = findNearestNeighbors(query, training, k = 3)

        // Then
        assertThat(result.map { it.sector })
            .containsExactly(RegionalZone.SOUTH, RegionalZone.WEST, RegionalZone.EAST)
            .inOrder()
        assertThat(result.map { it.distance }).containsExactly(1.0, 2.0, 3.0).inOrder()
    }

    @Test
    fun `given k equal to 1 when finding neighbors then returns only the nearest`() {
        // When
        val result = findNearestNeighbors(query, training, k = 1)

        // Then
        assertThat(result.map { it.sector }).containsExactly(RegionalZone.SOUTH)
    }

    @Test
    fun `given k larger than training size when finding neighbors then returns all sorted by distance`() {
        // When
        val result = findNearestNeighbors(query, training, k = 10)

        // Then
        assertThat(result.map { it.distance }).containsExactly(1.0, 2.0, 3.0, 4.0, 5.0).inOrder()
    }

    @Test
    fun `given empty training data when finding neighbors then returns empty list`() {
        // When
        val result = findNearestNeighbors(query, emptyList(), k = 5)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `given two records at the same distance when finding neighbors then keeps their original order`() {
        // Given
        val tied = listOf(
            LabeledPoint(listOf(1.0), RegionalZone.NORTH),    // distance 1
            LabeledPoint(listOf(-1.0), RegionalZone.SOUTH),   // distance 1
            LabeledPoint(listOf(2.0), RegionalZone.EAST)      // distance 2
        )

        // When
        val result = findNearestNeighbors(query, tied, k = 2)

        // Then
        assertThat(result.map { it.sector })
            .containsExactly(RegionalZone.NORTH, RegionalZone.SOUTH)
            .inOrder()
    }

    @Test
    fun `given three-dimensional normalized points when finding neighbors then nearest is record A`() {
        // Given: the example from the explanation
        val newPackage = listOf(0.05, 0.05, 0.625)
        val records = listOf(
            LabeledPoint(listOf(1.0, 1.0, 1.0), RegionalZone.NORTH),
            LabeledPoint(listOf(0.0, 0.0, 0.0), RegionalZone.SOUTH)
        )

        // When
        val result = findNearestNeighbors(newPackage, records, k = 2)

        // Then
        assertThat(result.map { it.sector })
            .containsExactly(RegionalZone.SOUTH, RegionalZone.NORTH)
            .inOrder()
        assertThat(result[0].distance).isWithin(0.001).of(0.629)
        assertThat(result[1].distance).isWithin(0.001).of(1.395)
    }

    @Test
    fun `given k equal to 0 when finding neighbors then throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            findNearestNeighbors(query, training, k = 0)
        }
    }
}