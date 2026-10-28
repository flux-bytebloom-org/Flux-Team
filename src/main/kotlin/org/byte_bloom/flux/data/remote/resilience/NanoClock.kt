package org.byte_bloom.flux.data.remote.resilience

fun interface NanoClock {
    fun nanoTime(): Long
}

object SystemNanoClock : NanoClock {
    override fun nanoTime(): Long = System.nanoTime()
}
