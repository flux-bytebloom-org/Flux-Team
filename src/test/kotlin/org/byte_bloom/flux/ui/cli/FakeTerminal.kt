package org.byte_bloom.flux.ui.cli

class FakeTerminal(vararg inputs: String) : Terminal {

    private val pendingInputs = ArrayDeque(inputs.toList())
    private val written = StringBuilder()

    fun output(): String = written.toString()

    override fun write(text: String) {
        written.append(text)
    }

    override fun writeLine(text: String) {
        written.appendLine(text)
    }

    override suspend fun readLine(prompt: String): String {
        write(prompt)
        return pendingInputs.removeFirst()
    }
}
