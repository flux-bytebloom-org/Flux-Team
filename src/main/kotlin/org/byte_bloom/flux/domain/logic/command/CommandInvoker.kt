package org.byte_bloom.flux.domain.logic.command

class CommandInvoker {

    private val history = ArrayDeque<Command>()

    fun executeCommand(command: Command) {
        command.execute()
        history.addLast(command)
    }

    fun undo(): Boolean {
        val lastCommand = history.removeLastOrNull() ?: return false
        lastCommand.undo()
        return true
    }

    fun clearHistory() {
        history.clear()
    }

    val historySize: Int
        get() = history.size
}
