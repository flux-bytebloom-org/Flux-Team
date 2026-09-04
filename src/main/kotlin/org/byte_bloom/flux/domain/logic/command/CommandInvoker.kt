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

    fun undoAll(): Boolean {
        if (history.isEmpty()) return false

        tailrec fun undoRemaining() {
            val command = history.removeLastOrNull() ?: return
            command.undo()
            undoRemaining()
        }

        undoRemaining()
        return true
    }

    fun clearHistory() {
        history.clear()
    }

    val historySize: Int
        get() = history.size
}
