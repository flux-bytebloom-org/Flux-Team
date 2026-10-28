package org.byte_bloom.flux.data.remote.resilience.retry

import kotlinx.coroutines.delay
import org.byte_bloom.flux.domain.exception.isTransient
import kotlin.coroutines.cancellation.CancellationException

fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    factor: Double = 2.0,
    block: () -> T
): Result<T> = retryWithBackoffInternal(maxRetries, initialDelayMs, factor, ::sleepInterruptibly, block)

internal fun <T> retryWithBackoffInternal(
    maxRetries: Int,
    initialDelayMs: Long,
    factor: Double,
    wait: (Long) -> Unit,
    block: () -> T
): Result<T> = runWithRetries(maxRetries, initialDelayMs, factor, wait, block)

internal suspend fun <T> retrySuspendWithBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T
): Result<T> = retrySuspendWithBackoffInternal(maxRetries, initialDelayMs, factor, { delay(it) }, block)

internal suspend fun <T> retrySuspendWithBackoffInternal(
    maxRetries: Int,
    initialDelayMs: Long,
    factor: Double,
    wait: suspend (Long) -> Unit,
    block: suspend () -> T
): Result<T> = runWithRetries(maxRetries, initialDelayMs, factor, { wait(it) }, { block() })

private inline fun <T> runWithRetries(
    maxRetries: Int,
    initialDelayMs: Long,
    factor: Double,
    wait: (Long) -> Unit,
    block: () -> T
): Result<T> {
    validateRetryArguments(maxRetries, initialDelayMs, factor)

    var attempt = 1
    var currentDelayMs = initialDelayMs

    while (true) {
        try {
            val value = block()
            logSuccess(attempt)
            return Result.success(value)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            val decision = decideRetry(exception, attempt, maxRetries, currentDelayMs)
            if (decision !is RetryDecision.Retry) return Result.failure(exception)

            wait(decision.delayMs)
            attempt++
            currentDelayMs = nextDelay(currentDelayMs, factor)
        }
    }
}

private sealed class RetryDecision {
    data class Retry(val delayMs: Long) : RetryDecision()
    data object Stop : RetryDecision()
}

private fun decideRetry(
    exception: Exception,
    attempt: Int,
    maxRetries: Int,
    currentDelayMs: Long
): RetryDecision {
    val totalAttempts = maxRetries + 1
    val failureTrace = "[RetryWithBackoff] Attempt $attempt/$totalAttempts failed: ${exception.describe()}"

    return when {
        !isTransient(exception) -> giveUp("$failureTrace -> not retryable, giving up.")
        attempt >= totalAttempts -> giveUp("$failureTrace -> retries exhausted, giving up.")
        else -> {
            println("$failureTrace -> retrying in ${currentDelayMs}ms.")
            RetryDecision.Retry(currentDelayMs)
        }
    }
}

private fun giveUp(message: String): RetryDecision {
    println(message)
    return RetryDecision.Stop
}

private fun Exception.describe(): String = "${javaClass.simpleName}: $message"

private fun logSuccess(attempt: Int) {
    println("[RetryWithBackoff] Success on attempt $attempt.")
}

private fun sleepInterruptibly(delayMs: Long) {
    try {
        Thread.sleep(delayMs)
    } catch (interrupted: InterruptedException) {
        Thread.currentThread().interrupt()
        throw CancellationException("Retry interrupted while waiting.", interrupted)
    }
}

private fun validateRetryArguments(maxRetries: Int, initialDelayMs: Long, factor: Double) {
    require(maxRetries >= 0) { "maxRetries must not be negative." }
    require(initialDelayMs >= 0) { "initialDelayMs must not be negative." }
    require(factor >= 1.0 && factor.isFinite()) { "factor must be finite and at least 1.0." }
}

private fun nextDelay(currentDelayMs: Long, factor: Double): Long =
    (currentDelayMs.toDouble() * factor)
        .coerceAtMost(Long.MAX_VALUE.toDouble())
        .toLong()