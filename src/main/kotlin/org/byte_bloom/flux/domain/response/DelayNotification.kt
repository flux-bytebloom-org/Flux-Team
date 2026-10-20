package org.byte_bloom.flux.domain.response

data class DelayNotification(
    val vehicleId: String,
    val consecutiveSlowReadings: Int,
    val lastSpeedKmh: Double,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)