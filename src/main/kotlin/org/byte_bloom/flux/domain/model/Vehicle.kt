package org.byte_bloom.flux.domain.model

data class Vehicle(
    val vehicleId: String,
    val currentHub: String,      // ربط مع كائن Warehouse
    val maxCapacityKg: Double,      // سعة التحميل بالكيلوجرام
    val costPerKm: Double           // التكلفة لكل كيلومتر
)
//currentHub to Warehouse
