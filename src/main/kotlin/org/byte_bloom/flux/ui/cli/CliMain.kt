package org.byte_bloom.flux.ui.cli

import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.di.cliModule
import org.byte_bloom.flux.di.networkModule
import org.byte_bloom.flux.di.repositoryModule
import org.byte_bloom.flux.di.useCaseModule
import org.byte_bloom.flux.di.validatorModule
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.ui.utils.NetworkLoader
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() = runBlocking<Unit> {
    startKoin {
        modules(networkModule, repositoryModule, validatorModule, useCaseModule, cliModule)
    }

    try {
        getKoin().get<LoadingIndicator>().whileLoading("Loading network data...") {
            getKoin().get<NetworkLoader>().load()
        }
        getKoin().get<CliShell>().run()
    } catch (e: Exception) {
        println("Fatal error: ${e.message}")
    } finally {
        shutDown()
    }
}

private suspend fun shutDown() {
    getKoin().get<CoroutineScope>().coroutineContext.job.cancelAndJoin()
    getKoin().get<HttpClient>().close()
    stopKoin()
}
