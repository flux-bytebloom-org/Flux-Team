package org.byte_bloom.flux.domain.model

import org.byte_bloom.flux.domain.model.PackageComponent

data class Package(
    val id: String,
    val weight: Double?,
    val originHub: Warehouse,
    val destinationHub: Warehouse,
    val priority: Priority
) : PackageComponent {

    override fun calculateTransitRate(baseRate: Double): Double {
        return baseRate
    }
}
