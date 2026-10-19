package org.byte_bloom.flux.ui.cli

interface CliCommand {
    val label: String
    suspend fun execute()
}
