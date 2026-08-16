package org.byte_bloom.flux.domain.model

private const val BASE_SHIPPING_COST = 0.0

data class Package(
    override val id: String,
    override val weight: Double?,
    override val originHub: Warehouse,
    override val destinationHub: Warehouse,
    override val priority: Priority
) : PackageComponent {

    override fun getDescription(): String = "Package $id"

    override fun getCost(): Double = BASE_SHIPPING_COST
}
