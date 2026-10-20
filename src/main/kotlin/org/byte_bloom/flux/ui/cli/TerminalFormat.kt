package org.byte_bloom.flux.ui.cli

private const val LINE_WIDTH = 48
private const val INDENT = "  "

fun Terminal.writeBanner(title: String) {
    val border = "=".repeat(LINE_WIDTH)
    writeLine(border)
    writeLine("$INDENT$title")
    writeLine(border)
}

fun Terminal.writeSection(title: String) {
    writeLine()
    writeLine("--- $title ".padEnd(LINE_WIDTH, '-'))
}

fun Terminal.writeIndented(text: String) = writeLine("$INDENT$text")
