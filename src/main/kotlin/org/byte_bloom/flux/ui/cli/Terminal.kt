package org.byte_bloom.flux.ui.cli

interface Terminal {
    fun write(text: String)
    fun writeLine(text: String = "")
    suspend fun readLine(prompt: String): String
}
