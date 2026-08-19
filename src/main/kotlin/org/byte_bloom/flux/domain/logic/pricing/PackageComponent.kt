package org.byte_bloom.flux.domain.logic.pricing

interface PackageComponent {

    fun calculateTransitRate(baseRate: Double): Double
}
