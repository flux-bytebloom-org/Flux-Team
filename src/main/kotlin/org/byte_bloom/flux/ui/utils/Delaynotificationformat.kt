package org.byte_bloom.flux.ui.utils

import org.byte_bloom.flux.domain.response.DelayNotification

fun DelayNotification.toDisplayLine(): String =
    "[DELAY] $vehicleId: $consecutiveSlowReadings slow readings in a row, " +
            "last speed ${"%.1f".format(lastSpeedKmh)} km/h " +
            "at (${"%.4f".format(latitude)}, ${"%.4f".format(longitude)})"
