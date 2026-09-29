package org.byte_bloom.flux.domain.algorithm.pricing.decorator

interface PackageComponent {

    fun getDescription(): String

    fun calculateTransitRate(baseRate: Double): Double
}

