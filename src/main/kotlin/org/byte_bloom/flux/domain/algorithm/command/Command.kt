package org.byte_bloom.flux.domain.algorithm.command

interface Command {
    fun execute()
    fun undo()
    fun describe(): String
}

