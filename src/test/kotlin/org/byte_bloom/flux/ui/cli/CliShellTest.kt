package org.byte_bloom.flux.ui.cli

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

private const val EXIT_OPTION = "0"
private const val FIRST_COMMAND_OPTION = "1"
private const val UNKNOWN_OPTION = "9"
private const val COMMAND_LABEL = "Fake command"
private const val MISSING_WAREHOUSE_ID = "X"
private const val NEVER = 0
private const val ONCE = 1

class CliShellTest {

    private val command = mockk<CliCommand>()

    init {
        every { command.label } returns COMMAND_LABEL
        coEvery { command.execute() } returns Unit
    }

    @Test
    fun `given exit option when run then does not execute any command`() = runBlocking<Unit> {
        val shell = CliShell(FakeTerminal(EXIT_OPTION), listOf(command))

        shell.run()

        coVerify(exactly = NEVER) { command.execute() }
    }

    @Test
    fun `given first option then exit when run then executes the command once`() = runBlocking<Unit> {
        val shell = CliShell(FakeTerminal(FIRST_COMMAND_OPTION, EXIT_OPTION), listOf(command))

        shell.run()

        coVerify(exactly = ONCE) { command.execute() }
    }

    @Test
    fun `given unknown option then exit when run then reports unknown option`() = runBlocking<Unit> {
        val terminal = FakeTerminal(UNKNOWN_OPTION, EXIT_OPTION)

        CliShell(terminal, listOf(command)).run()

        assertThat(terminal.output()).contains("Unknown option")
    }

    @Test
    fun `given command fails with logistics error when run then prints the error`() = runBlocking<Unit> {
        val terminal = FakeTerminal(FIRST_COMMAND_OPTION, EXIT_OPTION)
        val failure = LogisticsException.EntityNotFoundException.WarehouseNotFoundException(MISSING_WAREHOUSE_ID)
        coEvery { command.execute() } throws failure

        CliShell(terminal, listOf(command)).run()

        assertThat(terminal.output()).contains("Warehouse not found with id: $MISSING_WAREHOUSE_ID")
    }

    @Test
    fun `given command fails with an unexpected error when run then prints it and keeps running`() = runBlocking<Unit> {
        val terminal = FakeTerminal(FIRST_COMMAND_OPTION, EXIT_OPTION)
        coEvery { command.execute() } throws IllegalStateException("boom")

        CliShell(terminal, listOf(command)).run()

        assertThat(terminal.output()).contains("Unexpected error: boom")
        assertThat(terminal.output()).contains("Goodbye!")
    }

    @Test
    fun `given command is cancelled when run then the cancellation is not swallowed`() = runBlocking<Unit> {
        val terminal = FakeTerminal(FIRST_COMMAND_OPTION, EXIT_OPTION)
        coEvery { command.execute() } throws CancellationException("cancelled")

        assertFailsWith<CancellationException> {
            CliShell(terminal, listOf(command)).run()
        }
        assertThat(terminal.output()).doesNotContain("Unexpected error")
    }
}
