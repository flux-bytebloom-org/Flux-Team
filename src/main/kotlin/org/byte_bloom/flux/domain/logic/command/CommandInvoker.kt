package org.byte_bloom.flux.domain.logic.command

class CommandInvoker {

    private val undoStack = ArrayDeque<Command>()
    private val redoStack = ArrayDeque<Command>()

    fun executeCommand(command: Command) {
        println("[EXECUTE] $command")
        command.execute()
        undoStack.addLast(command)
        redoStack.clear()
    }

    fun undo(): Boolean {
        val command = undoStack.removeLastOrNull() ?: run {
            println("[UNDO] Nothing left to undo.")
            return false
        }
        println("[UNDO] $command")
        command.undo()
        redoStack.addLast(command)
        return true
    }

    fun redo(): Boolean {
        val command = redoStack.removeLastOrNull() ?: run {
            println("[REDO] Nothing left to redo.")
            return false
        }
        println("[REDO] $command")
        command.execute()
        undoStack.addLast(command)
        return true
    }

    fun undoSteps(steps: Int): Int =
        generateSequence { if (undo()) Unit else null }.take(steps).count()

    fun redoSteps(steps: Int): Int =
        generateSequence { if (redo()) Unit else null }.take(steps).count()

    fun clearHistory() {
        undoStack.clear()
        redoStack.clear()
    }

    val undoStackSize: Int get() = undoStack.size
    val redoStackSize: Int get() = redoStack.size
    val historySize: Int get() = undoStackSize
}
