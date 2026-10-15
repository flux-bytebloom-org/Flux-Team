package org.byte_bloom.flux.domain.algorithm.knn

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class MinMaxScalerTest {

    // Intentionally unordered: min = 10, max = 90
    private val scaler = MinMaxScaler(listOf(50.0, 10.0, 90.0))

    @Test
    fun `given value equal to min when scale then returns 0`() {
        // When
        val result = scaler.scale(10.0)

        // Then
        assertThat(result).isWithin(TOLERANCE).of(0.0)
    }

    @Test
    fun `given value equal to max when scale then returns 1`() {
        // When
        val result = scaler.scale(90.0)

        // Then
        assertThat(result).isWithin(TOLERANCE).of(1.0)
    }

    @Test
    fun `given value in the middle when scale then returns 0 point 5`() {
        // When
        val result = scaler.scale(50.0)

        // Then
        assertThat(result).isWithin(TOLERANCE).of(0.5)
    }

    @Test
    fun `given value at a quarter of the range when scale then returns 0 point 25`() {
        // When
        val result = scaler.scale(30.0)

        // Then
        assertThat(result).isWithin(TOLERANCE).of(0.25)
    }

    @Test
    fun `given value below min when scale then returns negative number`() {
        // When
        val result = scaler.scale(0.0)

        // Then
        assertThat(result).isWithin(TOLERANCE).of(-0.125)
    }

    @Test
    fun `given value above max when scale then returns more than 1`() {
        // When
        val result = scaler.scale(100.0)

        // Then
        assertThat(result).isWithin(TOLERANCE).of(1.125)
    }

    @Test
    fun `given all values equal when scale then returns 0 instead of dividing by zero`() {
        // Given
        val flatScaler = MinMaxScaler(listOf(5.0, 5.0, 5.0))

        // When
        val result = flatScaler.scale(9.0)

        // Then
        assertThat(result).isWithin(TOLERANCE).of(0.0)
    }

    @Test
    fun `given empty values when creating scaler then throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            MinMaxScaler(emptyList())
        }
    }

    private companion object {
        const val TOLERANCE = 1e-9
    }
}