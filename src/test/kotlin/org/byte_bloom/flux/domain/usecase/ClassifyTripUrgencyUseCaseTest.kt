package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.junit.jupiter.api.Test

private const val NO_PACKAGES = 0
private const val SINGLE_PACKAGE = 1
private const val SMALL_TRIP_SIZE = 3
private const val MEDIUM_TRIP_SIZE = 5

private const val OTHERS_FOR_EXACTLY_TWENTY_PERCENT = 4
private const val OTHERS_FOR_BELOW_TWENTY_PERCENT = 5
private const val LOW_PRIORITY_COUNT = 4

private const val URGENT_BELOW_HALF = 2
private const val OTHERS_BELOW_HALF = 3
private const val URGENT_ABOVE_HALF = 3
private const val OTHERS_ABOVE_HALF = 2

private const val ALMOST_ALL_URGENT = 4

private const val ZERO_THRESHOLD = 0.0
private const val HALF_THRESHOLD = 0.5
private const val FULL_THRESHOLD = 1.0
private const val ABOVE_FULL_THRESHOLD = 1.5

class ClassifyTripUrgencyUseCaseTest {

    private val useCase = ClassifyTripUrgencyUseCase()

    private fun packages(urgentCount: Int, otherCount: Int): List<Package> =
        List(urgentCount) { createTestPackage(id = "PKG-U$it", priority = Priority.URGENT) } +
                List(otherCount) { createTestPackage(id = "PKG-S$it", priority = Priority.STANDARD) }

    @Test
    fun `given all packages urgent when classify then returns true`() {
        // Given
        val allUrgent = packages(urgentCount = SMALL_TRIP_SIZE, otherCount = NO_PACKAGES)

        // When
        val result = useCase(allUrgent)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `given no urgent packages when classify then returns false`() {
        // Given
        val noUrgent = packages(urgentCount = NO_PACKAGES, otherCount = MEDIUM_TRIP_SIZE)

        // When
        val result = useCase(noUrgent)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `given empty package list when classify then returns false`() {
        // Given
        val emptyList = emptyList<Package>()

        // When
        val result = useCase(emptyList)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `given single urgent package when classify then returns true`() {
        // Given
        val single = packages(urgentCount = SINGLE_PACKAGE, otherCount = NO_PACKAGES)

        // When
        val result = useCase(single)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `given urgent ratio exactly at default threshold when classify then returns true`() {
        // Given
        val oneOfFive = packages(urgentCount = SINGLE_PACKAGE, otherCount = OTHERS_FOR_EXACTLY_TWENTY_PERCENT)

        // When
        val result = useCase(oneOfFive)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `given urgent ratio below default threshold when classify then returns false`() {
        // Given
        val oneOfSix = packages(urgentCount = SINGLE_PACKAGE, otherCount = OTHERS_FOR_BELOW_TWENTY_PERCENT)

        // When
        val result = useCase(oneOfSix)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `given low priority packages only when classify then returns false`() {
        // Given
        val lowOnly = List(LOW_PRIORITY_COUNT) { createTestPackage(id = "PKG-L$it", priority = Priority.LOW) }

        // When
        val result = useCase(lowOnly)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `given custom threshold not reached when classify then returns false`() {
        // Given
        val twoOfFive = packages(urgentCount = URGENT_BELOW_HALF, otherCount = OTHERS_BELOW_HALF)

        // When
        val result = useCase(twoOfFive, urgencyThreshold = HALF_THRESHOLD)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `given custom threshold reached when classify then returns true`() {
        // Given
        val threeOfFive = packages(urgentCount = URGENT_ABOVE_HALF, otherCount = OTHERS_ABOVE_HALF)

        // When
        val result = useCase(threeOfFive, urgencyThreshold = HALF_THRESHOLD)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `given zero threshold and no urgent packages when classify then returns true`() {
        // Given
        val noUrgent = packages(urgentCount = NO_PACKAGES, otherCount = SMALL_TRIP_SIZE)

        // When
        val result = useCase(noUrgent, urgencyThreshold = ZERO_THRESHOLD)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `given threshold of one when classify then requires all packages urgent`() {
        // Given
        val almostAll = packages(urgentCount = ALMOST_ALL_URGENT, otherCount = SINGLE_PACKAGE)
        val all = packages(urgentCount = MEDIUM_TRIP_SIZE, otherCount = NO_PACKAGES)

        // When
        val almostAllResult = useCase(almostAll, urgencyThreshold = FULL_THRESHOLD)
        val allResult = useCase(all, urgencyThreshold = FULL_THRESHOLD)

        // Then
        assertThat(almostAllResult).isFalse()
        assertThat(allResult).isTrue()
    }

    @Test
    fun `given threshold above one when classify then returns false even if all urgent`() {
        // Given
        val allUrgent = packages(urgentCount = SMALL_TRIP_SIZE, otherCount = NO_PACKAGES)

        // When
        val result = useCase(allUrgent, urgencyThreshold = ABOVE_FULL_THRESHOLD)

        // Then
        assertThat(result).isFalse()
    }
}
