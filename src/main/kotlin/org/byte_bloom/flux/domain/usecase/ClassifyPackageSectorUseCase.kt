package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.algorithm.knn.LabeledPoint
import org.byte_bloom.flux.domain.algorithm.knn.MinMaxScaler
import org.byte_bloom.flux.domain.algorithm.knn.findNearestNeighbors
import org.byte_bloom.flux.domain.algorithm.knn.majorityVote
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.CargoQuery
import org.byte_bloom.flux.domain.model.CargoRecord
import org.byte_bloom.flux.domain.model.RegionalZone

class ClassifyPackageSectorUseCase(private val k: Int = DEFAULT_K) {

    init {
        require(k > 0) { "k must be positive" }
    }

    operator fun invoke(query: CargoQuery, trainingRecords: List<CargoRecord>): RegionalZone {
        if (trainingRecords.isEmpty()) {
            throw LogisticsException.BusinessLogicException.InsufficientTrainingDataException()
        }

        val latitudeScaler = MinMaxScaler(trainingRecords.map { it.latitude })
        val longitudeScaler = MinMaxScaler(trainingRecords.map { it.longitude })
        val weightScaler = MinMaxScaler(trainingRecords.map { it.weightKg })

        val trainingPoints = trainingRecords.map { record ->
            LabeledPoint(
                features = listOf(
                    latitudeScaler.scale(record.latitude),
                    longitudeScaler.scale(record.longitude),
                    weightScaler.scale(record.weightKg)
                ),
                sector = record.sector
            )
        }

        val queryPoint = listOf(
            latitudeScaler.scale(query.latitude),
            longitudeScaler.scale(query.longitude),
            weightScaler.scale(query.weightKg)
        )

        val neighbors = findNearestNeighbors(queryPoint, trainingPoints, k)
        return majorityVote(neighbors)
    }

    companion object {
        const val DEFAULT_K = 5
    }
}