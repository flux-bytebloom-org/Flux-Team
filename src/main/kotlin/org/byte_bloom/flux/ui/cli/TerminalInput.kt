package org.byte_bloom.flux.ui.cli

private const val INVALID_NUMBER_MESSAGE = "Please enter a valid number."
private const val INVALID_ANSWER_MESSAGE = "Please answer with y or n."
private val YES_ANSWERS = setOf("y", "yes")
private val NO_ANSWERS = setOf("n", "no")

suspend fun Terminal.readTrimmed(prompt: String): String = readLine(prompt).trim()

suspend fun Terminal.readDouble(prompt: String): Double {
    var number = readTrimmed(prompt).toDoubleOrNull()
    while (number == null) {
        writeLine(INVALID_NUMBER_MESSAGE)
        number = readTrimmed(prompt).toDoubleOrNull()
    }
    return number
}

suspend fun Terminal.readYesNo(prompt: String): Boolean {
    var answer = readTrimmed(prompt).lowercase()
    while (answer !in YES_ANSWERS && answer !in NO_ANSWERS) {
        writeLine(INVALID_ANSWER_MESSAGE)
        answer = readTrimmed(prompt).lowercase()
    }
    return answer in YES_ANSWERS
}
