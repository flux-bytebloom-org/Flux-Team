package org.byte_bloom.flux.domain.model

interface PackageComponent {
    val id: String
    val weight: Double?
    val originHub: Warehouse
    val destinationHub: Warehouse
    val priority: Priority

    fun getDescription(): String
    fun getCost(): Double
}
