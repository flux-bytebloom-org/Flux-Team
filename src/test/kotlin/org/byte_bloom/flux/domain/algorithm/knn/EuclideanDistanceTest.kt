package org.byte_bloom.flux.domain.algorithm.knn

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class EuclideanDistanceTest {

    @Test
    fun `given identical points when computing distance then returns 0`() {
        // When
        val result = euclideanDistance(listOf(0.2, 0.4, 0.6), listOf(0.2, 0.4, 0.6))

        // Then
        assertThat(result).isWithin(TOLERANCE).of(0.0)
    }

    @Test
    fun `given points that differ in one dimension when computing distance then returns that difference`() {
        // When
        val result = euclideanDistance(listOf(0.0), listOf(3.0))

        // Then
        assertThat(result).isWithin(TOLERANCE).of(3.0)
    }

    @Test
    fun `given a 3-4-5 triangle when computing distance then returns 5`() {
        // When
        val result = euclideanDistance(listOf(0.0, 0.0), listOf(3.0, 4.0))

        // Then
        assertThat(result).isWithin(TOLERANCE).of(5.0)
    }

    @Test
    fun `given opposite corners of the unit cube when computing distance then returns square root of 3`() {
        // When
        val result = euclideanDistance(listOf(0.0, 0.0, 0.0), listOf(1.0, 1.0, 1.0))

        // Then
        assertThat(result).isWithin(TOLERANCE).of(Math.sqrt(3.0))
    }

    @Test
    fun `given negative coordinates when computing distance then sign does not matter`() {
        // When
        val result = euclideanDistance(listOf(-1.0, -1.0), listOf(2.0, 3.0))

        // Then
        assertThat(result).isWithin(TOLERANCE).of(5.0)
    }

    @Test
    fun `given two points when swapping them then distance is the same`() {
        // Given
        val a = listOf(0.1, 0.7, 0.3)
        val b = listOf(0.9, 0.2, 0.5)

        // When / Then
        assertThat(euclideanDistance(a, b)).isWithin(TOLERANCE).of(euclideanDistance(b, a))
    }

    @Test
    fun `given the normalized example from the explanation when computing distances then nearest is record A`() {
        // Given: new package normalized, and records A and B normalized
        val newPackage = listOf(0.05, 0.05, 0.625)
        val recordA = listOf(0.0, 0.0, 0.0)
        val recordB = listOf(1.0, 1.0, 1.0)

        // When
        val toA = euclideanDistance(newPackage, recordA)
        val toB = euclideanDistance(newPackage, recordB)

        // Then
        assertThat(toA).isWithin(0.001).of(0.629)
        assertThat(toB).isWithin(0.001).of(1.395)
        assertThat(toA).isLessThan(toB)
    }

    @Test
    fun `given points of different sizes when computing distance then throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            euclideanDistance(listOf(1.0, 2.0), listOf(1.0, 2.0, 3.0))
        }
    }

    private companion object {
        const val TOLERANCE = 1e-9
    }
}