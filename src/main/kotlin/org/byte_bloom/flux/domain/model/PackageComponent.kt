package org.byte_bloom.flux.domain.model

interface PackageComponent {

    fun calculateTransitRate(baseRate: Double): Double
}
