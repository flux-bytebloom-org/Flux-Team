package org.byte_bloom.flux.domain.logic.command

interface Command {
    fun execute()
    fun undo()
}

