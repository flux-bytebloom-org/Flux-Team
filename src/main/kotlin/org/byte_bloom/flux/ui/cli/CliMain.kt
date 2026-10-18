package org.byte_bloom.flux.ui.cli

import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.di.cliModule
import org.byte_bloom.flux.di.networkModule
import org.byte_bloom.flux.di.repositoryModule
import org.byte_bloom.flux.di.useCaseModule
import org.byte_bloom.flux.di.validatorModule
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() = runBlocking<Unit> {
    startKoin {
        modules(networkModule, repositoryModule, validatorModule, useCaseModule, cliModule)
    }
    val shell: CliShell = getKoin().get()
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    try {
        applicationScope.async { shell.run() }.await()
    } finally {
        shutDown(applicationScope)
    }
}

private fun shutDown(applicationScope: CoroutineScope) {
    applicationScope.cancel()
    getKoin().get<HttpClient>().close()
    stopKoin()
}
