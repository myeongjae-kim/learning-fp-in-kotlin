package org.kiworkshop.learningfpinkotlin

fun String.head(): Char = first()
fun String.tail(): String = drop(1)

fun <T> List<T>.head(): T = first()
fun <T> List<T>.tail(): List<T> = drop(1)
