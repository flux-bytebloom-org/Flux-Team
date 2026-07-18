package org.byte_bloom.flux


fun main() {
    val packages = readPackages("packages.csv")

    val sorted = selectionSort(packages)

    sorted.take(3).forEach {
        println(it)
    }
}