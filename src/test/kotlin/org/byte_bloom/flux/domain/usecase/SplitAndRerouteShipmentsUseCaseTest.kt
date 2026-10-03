package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.WeightedPath
import org.byte_bloom.flux.domain.testdata.aPackage
import org.byte_bloom.flux.domain.testdata.aWarehouse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SplitAndRerouteShipmentsUseCaseTest {

    private val reroute = mockk<ReroutePackageUseCase>()
    private val useCase = SplitAndRerouteShipmentsUseCase(reroute)

    private val origin = aWarehouse(id = "WH-ORIGIN")
    private val destination = aWarehouse(id = "WH-DEST")
    private val otherDestination = aWarehouse(id = "WH-OTHER")
    private val viaHub = aWarehouse(id = "WH-VIA")

    private val originalPath = WeightedPath(path = listOf(origin, destination), packageCount = 1)
    private val newPath = listOf(origin, viaHub, destination)

    @BeforeEach
    fun stubRerouteToReturnThePackageItWasGiven() {
        every { reroute(any(), any(), any()) } answers { secondArg() }
    }

    // ---------- Early return ----------

    @Test
    fun `returns the inputs untouched and reroutes nothing when the ratio moves no package`() {
        // Given
        val all = listOf(queued("PKG-1"), queued("PKG-2"))
        val plans = mapOf("PKG-OLD" to listOf(origin, destination))

        // When
        val result = useCase(originalPath, newPath, 0.0, all, plans)

        // Then
        assertThat(result.reroutedPackages).isSameInstanceAs(all)
        assertThat(result.updatedRoutePlans).isSameInstanceAs(plans)
        verify(exactly = 0) { reroute(any(), any(), any()) }
    }

    // ---------- How many packages move ----------

    @Test
    fun `moves the truncated share of matching packages, not the rounded one`() {
        // Given: 7 * 0.5 = 3.5 -> 3 (rounding would give 4)
        val all = (1..7).map { queued("PKG-$it") }

        // When
        val result = useCase(originalPath, newPath, 0.5, all, emptyMap())

        // Then
        assertThat(result.updatedRoutePlans).hasSize(3)
        verify(exactly = 3) { reroute(origin, any(), any()) }
    }

    @Test
    fun `moves every matching package when the ratio is above one`() {
        // Given
        val all = (1..4).map { queued("PKG-$it") }

        // When
        val result = useCase(originalPath, newPath, 1.5, all, emptyMap())

        // Then
        assertThat(result.updatedRoutePlans.keys)
            .containsExactly("PKG-1", "PKG-2", "PKG-3", "PKG-4")
        verify(exactly = 4) { reroute(origin, any(), any()) }
    }

    @Test
    fun `moves nothing when the ratio is negative`() {
        // Given
        val all = (1..4).map { queued("PKG-$it") }

        // When
        val result = useCase(originalPath, newPath, -0.5, all, emptyMap())

        // Then
        assertThat(result.updatedRoutePlans).isEmpty()
        verify(exactly = 0) { reroute(any(), any(), any()) }
    }

    // ---------- Which packages move ----------

    @Test
    fun `moves the lowest priority packages first`() {
        // Given: sorted ascending the order is PKG-2, PKG-4 (LOW), PKG-3 (STANDARD), PKG-1 (URGENT)
        val all = listOf(
            queued("PKG-1", Priority.URGENT),
            queued("PKG-2", Priority.LOW),
            queued("PKG-3", Priority.STANDARD),
            queued("PKG-4", Priority.LOW)
        )

        // When: 4 * 0.5 = 2 packages move
        val result = useCase(originalPath, newPath, 0.5, all, emptyMap())

        // Then
        assertThat(result.updatedRoutePlans.keys).containsExactly("PKG-2", "PKG-4")
    }

    @Test
    fun `only considers packages headed to the path destination`() {
        // Given
        val all = listOf(
            queued("PKG-1"),
            queued("PKG-2"),
            queued("PKG-3", toDestination = otherDestination),
            queued("PKG-4", toDestination = otherDestination)
        )

        // When: ratio 1.0 would move everything that matches
        val result = useCase(originalPath, newPath, 1.0, all, emptyMap())

        // Then: the other-destination packages are neither rerouted nor lost
        assertThat(result.updatedRoutePlans.keys).containsExactly("PKG-1", "PKG-2")
        assertThat(result.reroutedPackages.map { it.id })
            .containsExactly("PKG-1", "PKG-2", "PKG-3", "PKG-4")
    }

    // ---------- Route plans ----------

    @Test
    fun `records the new path for each moved package and keeps existing plans`() {
        // Given
        val existingPlan = listOf(origin, destination)
        val all = listOf(queued("PKG-1"), queued("PKG-2"))

        // When
        val result = useCase(originalPath, newPath, 1.0, all, mapOf("PKG-OLD" to existingPlan))

        // Then
        assertThat(result.updatedRoutePlans["PKG-1"]).isEqualTo(newPath)
        assertThat(result.updatedRoutePlans["PKG-2"]).isEqualTo(newPath)
        assertThat(result.updatedRoutePlans["PKG-OLD"]).isEqualTo(existingPlan)
    }

    // ---------- Result packages ----------

    @Test
    fun `returns the packages produced by the reroute use case in place of the originals`() {
        // Given: the use case marks every rerouted package with weight 99.0
        every { reroute(any(), any(), any()) } answers { secondArg<Package>().copy(weight = 99.0) }
        val all = listOf(
            queued("PKG-1", Priority.LOW),
            queued("PKG-2", Priority.LOW),
            queued("PKG-3", Priority.STANDARD),
            queued("PKG-4", Priority.URGENT)
        )

        // When: PKG-1 and PKG-2 move
        val result = useCase(originalPath, newPath, 0.5, all, emptyMap())

        // Then
        val movedInResult = result.reroutedPackages.filter { it.id in setOf("PKG-1", "PKG-2") }
        assertThat(movedInResult.map { it.weight }).containsExactly(99.0, 99.0)
    }

    @Test
    fun `lists every package exactly once`() {
        // Given
        val all = (1..4).map { queued("PKG-$it") }

        // When: half move, half stay
        val result = useCase(originalPath, newPath, 0.5, all, emptyMap())

        // Then: containsExactly counts duplicates, so a repeated package fails here
        assertThat(result.reroutedPackages.map { it.id })
            .containsExactly("PKG-1", "PKG-2", "PKG-3", "PKG-4")
    }

    // ---------- Helpers ----------

    /** Creates a package, adds it to the origin's cargo queue, and returns it. */
    private fun queued(
        id: String,
        priority: Priority = Priority.STANDARD,
        toDestination: Warehouse = destination
    ): Package =
        aPackage(id = id, origin = origin, destination = toDestination, priority = priority)
            .also { origin.addPackage(it) }
}
