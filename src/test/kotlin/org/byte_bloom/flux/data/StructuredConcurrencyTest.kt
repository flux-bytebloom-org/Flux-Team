package org.byte_bloom.flux.data

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.byte_bloom.flux.domain.exception.LogisticsException.BusinessLogicException.IllegalStateTransitionException
import org.byte_bloom.flux.domain.exception.LogisticsException.ValidationException.InvalidPackageWeightException
import org.byte_bloom.flux.domain.exception.LogisticsException.BusinessLogicException.VehicleCapacityExceededException
import org.byte_bloom.flux.domain.exception.suspendRunCatching
import org.byte_bloom.flux.domain.state.CreatedState
import org.byte_bloom.flux.domain.state.InTransitState
import org.byte_bloom.flux.domain.state.Shipment
import org.byte_bloom.flux.domain.testdata.aVehicle
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.assertFailsWith

class StructuredConcurrencyTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun suspendRunCatchingDoesNotSwallowCancellation() = runTest {
        var reachedAfter = false
        val job = launch {
            suspendRunCatching { delay(10_000) }
            reachedAfter = true
        }
        runCurrent()
        job.cancelAndJoin()
        assertFalse(reachedAfter)
    }

    @Test
    fun failureCancelsSibling() = runTest {
        var siblingCancelled = false
        val result = runCatching {
            coroutineScope {
                launch {
                    try { delay(10_000) }
                    catch (e: CancellationException) {
                        siblingCancelled = true
                        throw e
                    }
                }
                async { delay(100); error("boom") }.await()
            }
        }
        assertTrue(result.isFailure)
        assertTrue(siblingCancelled)
    }
}
