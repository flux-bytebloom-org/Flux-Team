package org.byte_bloom.flux.data.parsers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class FleetParseTest {

    @Test
    fun `parseFleet returns valid vehicle when line has all required data`() {
        val lines = listOf("V1,H1,100.0,50.0")

        val result = parseFleet(lines)

        assertEquals(1, result.size)
        assertEquals("V1", result[0].vehicleId)
        assertEquals("H1", result[0].currentHubId)
        assertEquals(100.0, result[0].maxCapacityKg)
        assertEquals(50.0, result[0].costPerKm)
    }

    @Test
    fun `parseFleet returns null for line with wrong column count`() {
        val lines = listOf("V1,H1,100.0")

        val result = parseFleet(lines)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseFleet returns null when vehicleId is empty`() {
        val lines = listOf(",H1,100.0,50.0")

        val result = parseFleet(lines)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseFleet returns null when hubId is empty`() {
        val lines = listOf("V1,,100.0,50.0")

        val result = parseFleet(lines)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseFleet uses default value when capacity is invalid`() {
        val lines = listOf("V1,H1,notANumber,50.0")

        val result = parseFleet(lines)

        assertEquals(1, result.size)
        assertEquals(-1.0, result[0].maxCapacityKg)
    }

    @Test
    fun `parseFleet uses default value when cost is invalid`() {
        val lines = listOf("V1,H1,100.0,notANumber")

        val result = parseFleet(lines)

        assertEquals(1, result.size)
        assertEquals(-1.0, result[0].costPerKm)
    }

    @Test
    fun `parseFleet returns only valid vehicles from mixed lines`() {
        val lines = listOf(
            "V1,H1,100.0,50.0",
            "V2,H2,200.0",
            ",H3,300.0,60.0",
            "V4,H4,400.0,70.0"
        )

        val result = parseFleet(lines)

        assertEquals(2, result.size)
        assertEquals("V1", result[0].vehicleId)
        assertEquals("V4", result[1].vehicleId)
    }

    @Test
    fun `parseFleet returns empty list when given no lines`() {
        val lines = emptyList<String>()

        val result = parseFleet(lines)

        assertTrue(result.isEmpty())
    }
}
