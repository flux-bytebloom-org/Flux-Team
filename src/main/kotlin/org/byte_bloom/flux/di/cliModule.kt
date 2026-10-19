package org.byte_bloom.flux.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.byte_bloom.flux.ui.cli.CliCommand
import org.byte_bloom.flux.ui.cli.CliShell
import org.byte_bloom.flux.ui.cli.LoadingIndicator
import org.byte_bloom.flux.ui.cli.RouteCalculationCommand
import org.byte_bloom.flux.ui.cli.SystemTerminal
import org.byte_bloom.flux.ui.cli.Terminal
import org.koin.dsl.module

val cliModule = module {

    single<Terminal> { SystemTerminal() }
    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    single { LoadingIndicator(get()) }

    factory { RouteCalculationCommand(get(), get(), get(), get()) }

    factory {
        CliShell(
            terminal = get(),
            commands = listOf<CliCommand>(
                get<RouteCalculationCommand>()
            )
        )
    }
}
