package org.byte_bloom.flux.domain.algorithm.knn

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.model.RegionalZone
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class MajorityVoteTest {

    @Test
    fun `given a clear majority when voting then returns the most frequent sector`() {
        // Given
        val neighbors = listOf(
            Neighbor(RegionalZone.NORTH, 0.1),
            Neighbor(RegionalZone.NORTH, 0.2),
            Neighbor(RegionalZone.SOUTH, 0.3),
            Neighbor(RegionalZone.NORTH, 0.4),
            Neighbor(RegionalZone.SOUTH, 0.5)
        )

        // When
        val result = majorityVote(neighbors)

        // Then
        assertThat(result).isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `given the nearest neighbor is a minority when voting then count beats distance`() {
        // Given
        val neighbors = listOf(
            Neighbor(RegionalZone.SOUTH, 0.1),
            Neighbor(RegionalZone.NORTH, 0.2),
            Neighbor(RegionalZone.NORTH, 0.3),
            Neighbor(RegionalZone.NORTH, 0.4),
            Neighbor(RegionalZone.SOUTH, 0.5)
        )

        // When
        val result = majorityVote(neighbors)

        // Then
        assertThat(result).isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `given all neighbors from the same sector when voting then returns that sector`() {
        // Given
        val neighbors = List(5) { Neighbor(RegionalZone.EAST, it.toDouble()) }

        // When
        val result = majorityVote(neighbors)

        // Then
        assertThat(result).isEqualTo(RegionalZone.EAST)
    }

    @Test
    fun `given a single neighbor when voting then returns its sector`() {
        // When
        val result = majorityVote(listOf(Neighbor(RegionalZone.WEST, 0.7)))

        // Then
        assertThat(result).isEqualTo(RegionalZone.WEST)
    }

    @Test
    fun `given a tie on count when voting then the sector with the nearest neighbor wins`() {
        // Given: NORTH and SOUTH both have 2 votes, SOUTH has the closest neighbor (0.1)
        val neighbors = listOf(
            Neighbor(RegionalZone.SOUTH, 0.1),
            Neighbor(RegionalZone.NORTH, 0.5),
            Neighbor(RegionalZone.NORTH, 0.6),
            Neighbor(RegionalZone.SOUTH, 0.7),
            Neighbor(RegionalZone.WEST, 0.9)
        )

        // When
        val result = majorityVote(neighbors)

        // Then
        assertThat(result).isEqualTo(RegionalZone.SOUTH)
    }

    @Test
    fun `given a tie and unsorted neighbors when voting then still picks the sector with the nearest neighbor`() {
        // Given: NORTH appears first in the list, but SOUTH has the closest neighbor
        val neighbors = listOf(
            Neighbor(RegionalZone.NORTH, 0.5),
            Neighbor(RegionalZone.NORTH, 0.6),
            Neighbor(RegionalZone.SOUTH, 0.7),
            Neighbor(RegionalZone.SOUTH, 0.1),
            Neighbor(RegionalZone.WEST, 0.9)
        )

        // When
        val result = majorityVote(neighbors)

        // Then
        assertThat(result).isEqualTo(RegionalZone.SOUTH)
    }

    @Test
    fun `given every sector has one vote when voting then the nearest neighbor decides`() {
        // Given
        val neighbors = listOf(
            Neighbor(RegionalZone.EAST, 0.3),
            Neighbor(RegionalZone.WEST, 0.2),
            Neighbor(RegionalZone.NORTH, 0.4)
        )

        // When
        val result = majorityVote(neighbors)

        // Then
        assertThat(result).isEqualTo(RegionalZone.WEST)
    }

    @Test
    fun `given a full tie on count and distance when voting then the first sector in the list wins`() {
        // Given
        val neighbors = listOf(
            Neighbor(RegionalZone.NORTH, 0.5),
            Neighbor(RegionalZone.SOUTH, 0.5)
        )

        // When
        val result = majorityVote(neighbors)

        // Then
        assertThat(result).isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `given no neighbors when voting then throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            majorityVote(emptyList())
        }
    }
}