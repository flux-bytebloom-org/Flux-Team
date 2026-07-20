package org.byte_bloom.flux.utils.parsers


fun cleanLines(
    rawLines: List<String>
): List<String> {

    return rawLines
        .drop(1)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}
