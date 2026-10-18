package org.byte_bloom.flux.domain.algorithm.knn

class MinMaxScaler(values: List<Double>) {

    init {
        require(values.isNotEmpty()) { "Cannot build a scaler from an empty list" }
    }

    private val min = values.min()
    private val max = values.max()

    fun scale(value: Double): Double {
        val range = max - min
        return if (range == 0.0) 0.0 else (value - min) / range
    }
}