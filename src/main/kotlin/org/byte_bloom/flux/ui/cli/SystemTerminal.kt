package org.byte_bloom.flux.ui.cli

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible

private const val INPUT_CLOSED_MESSAGE = "Standard input was closed"

class SystemTerminal : Terminal {

    override fun write(text: String) {
        print(text)
        System.out.flush()
    }

    override fun writeLine(text: String) {
        println(text)
    }

    // Reading stdin blocks a thread, so it runs on IO and stays cancellable.
    override suspend fun readLine(prompt: String): String {
        write(prompt)
        val line = runInterruptible(Dispatchers.IO) { readlnOrNull() }
        return line ?: error(INPUT_CLOSED_MESSAGE)
    }
}
