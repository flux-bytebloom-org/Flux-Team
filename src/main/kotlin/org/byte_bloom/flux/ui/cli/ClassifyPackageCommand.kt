package org.byte_bloom.flux.ui.cli

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.mapper.toCargoRecords
import org.byte_bloom.flux.domain.model.CargoQuery
import org.byte_bloom.flux.domain.usecase.ClassifyPackageSectorUseCase
import org.byte_bloom.flux.ui.utils.NetworkLoader
import org.byte_bloom.flux.ui.utils.NetworkSnapshot


class ClassifyPackageCommand(
    private val terminal: Terminal,
    private val networkLoader: NetworkLoader,
    private val classifySector: ClassifyPackageSectorUseCase
) : CliCommand {

    override val label = "Classify a new package into a regional sector (KNN)"

    override suspend fun execute() {
        val network = networkLoader.load()
        printDescription(network)

        val latitude = terminal.readDouble("Destination latitude in range [-90.0,90,0] : ")
        if (latitude > 90 || latitude <-90) {
            throw LogisticsException.ValidationException.InvalidPackageWeightException(
                "latitude must be in range [-90.0,90,0]"
            )
        }
        val longitude = terminal.readDouble("Destination longitude in range [-180.0,180.0] : ")
        if (longitude > 180 || longitude < -180 ) {
            throw LogisticsException.ValidationException.InvalidPackageWeightException(
                "longitude must be in range [-180.0,180.0]"
            )
        }
        val weightKg = terminal.readDouble("Weight (kg): ")
        if (weightKg <= 0.0) {
            throw LogisticsException.ValidationException.InvalidPackageWeightException(
                "weight must be greater than zero"
            )
        }

        val trainingRecords = network.packages.toCargoRecords()
        val sector = withContext(Dispatchers.Default) {
            classifySector(CargoQuery(latitude, longitude, weightKg), trainingRecords)
        }

        terminal.writeLine("Predicted sector: $sector (trained on ${trainingRecords.size} known packages)")
    }

    private fun printDescription(network: NetworkSnapshot) {
        terminal.writeSection("Predicts regional sector")
        terminal.writeIndented("Predicts the delivery sector of a new package from its destination coordinates and weight.")
        network.warehouses.firstOrNull()?.let {
            terminal.writeIndented("Tip: ${it.id} is located at (${it.latitude}, ${it.longitude})")
        }
        terminal.writeLine()
    }
}
