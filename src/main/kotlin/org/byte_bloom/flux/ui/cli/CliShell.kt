package org.byte_bloom.flux.ui.cli

import org.byte_bloom.flux.domain.exception.LogisticsException
import kotlin.coroutines.cancellation.CancellationException

private const val EXIT_OPTION = "0"
private const val FIRST_MENU_NUMBER = 1
private const val CHOICE_PROMPT = "Choose an option: "
private const val WELCOME_MESSAGE = "Flux Logistics Shell"
private const val GOODBYE_MESSAGE = "Goodbye!"
private const val UNKNOWN_OPTION_MESSAGE = "Unknown option, please choose a number from the menu."

class CliShell(
    private val terminal: Terminal,
    private val commands: List<CliCommand>
) {

    suspend fun run() {
        terminal.writeBanner(WELCOME_MESSAGE)
        var choice = promptChoice()
        while (choice != EXIT_OPTION) {
            runChoice(choice)
            choice = promptChoice()
        }
        terminal.writeLine(GOODBYE_MESSAGE)
    }

    private suspend fun promptChoice(): String {
        printMenu()
        return terminal.readTrimmed(CHOICE_PROMPT)
    }

    private fun printMenu() {
        terminal.writeLine()
        commands.forEachIndexed { index, command ->
            terminal.writeIndented("${index + FIRST_MENU_NUMBER}. ${command.label}")
        }
        terminal.writeIndented("$EXIT_OPTION. Exit")
        terminal.writeLine()
    }

    private suspend fun runChoice(choice: String) {
        val command = findCommand(choice)
        if (command == null) {
            terminal.writeLine(UNKNOWN_OPTION_MESSAGE)
        } else {
            executeSafely(command)
        }
    }

    private fun findCommand(choice: String): CliCommand? =
        choice.toIntOrNull()?.let { commands.getOrNull(it - FIRST_MENU_NUMBER) }

    private suspend fun executeSafely(command: CliCommand) {
        try {
            command.execute()
        } catch (e: CancellationException) {
            throw e
        } catch (exception: LogisticsException) {
            terminal.writeIndented("Error: ${exception.message}")
        } catch (e: Exception) {
            terminal.writeIndented("Unexpected error: ${e.message}")
        }
    }
}
