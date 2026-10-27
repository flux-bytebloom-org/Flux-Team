package org.byte_bloom.flux.domain.algorithm.circuitbreaker

fun interface NanoClock {
    fun nanoTime(): Long
}

object SystemNanoClock : NanoClock {
    override fun nanoTime(): Long = System.nanoTime()
}
