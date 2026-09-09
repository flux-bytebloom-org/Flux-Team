package org.byte_bloom.flux.domain.logic.command

class CommandInvoker {

    private val undoStack = ArrayDeque<Command>()
    private val redoStack = ArrayDeque<Command>()

    fun executeCommand(command: Command) {
        command.execute()
        undoStack.addLast(command)
        redoStack.clear()
    }

    fun undo(): Boolean {
        val command = undoStack.removeLastOrNull() ?: run {
            println("[UNDO] Nothing to undo.")
            return false
        }
        command.undo()
        redoStack.addLast(command)
        return true
    }

    fun redo(): Boolean {
        val command = redoStack.removeLastOrNull() ?: run {
            println("[REDO] Nothing to redo.")
            return false
        }
        command.execute()
        undoStack.addLast(command)
        return true
    }


    fun undoAll(): Boolean {
        if (undoStack.isEmpty()) return false
        undoRemaining()
        return true
    }
    private fun undoRemaining() {
        val command = undoStack.removeLastOrNull() ?: return
        command.undo()
        redoStack.addLast(command)
        undoRemaining()
    }

    fun clearHistory() {
        undoStack.clear()
        redoStack.clear()
    }
}
