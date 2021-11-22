package org.kiworkshop.learningfpinkotlin

import org.kiworkshop.learningfpinkotlin.FunStream.Cons
import org.kiworkshop.learningfpinkotlin.FunStream.Nil

sealed class FunStream<out T> {
    object Nil : FunStream<Nothing>()
    data class Cons<out T>(val head: () -> T, val tail: () -> FunStream<T>) : FunStream<T>()
}

fun <T> FunStream<T>.getHead(): T = when (this) {
    Nil -> throw NoSuchElementException()
    is Cons -> head()
}

fun <T> FunStream<T>.getTail(): FunStream<T> = when (this) {
    Nil -> throw NoSuchElementException()
    is Cons -> tail()
}

fun <T> funStreamOf(vararg elements: T): FunStream<T> = elements.toFunStream()

private fun <T> Array<out T>.toFunStream(): FunStream<T> = when {
    this.isEmpty() -> Nil
    else -> Cons({ this[0] }) { this.copyOfRange(1, this.size).toFunStream() }
}

tailrec fun FunStream<Int>.sum(acc: Int = 0): Int = when (this) {
    Nil -> acc
    is Cons -> getTail().sum(acc + getHead())
}

tailrec fun FunStream<Int>.product(acc: Int = 1): Int = when (this) {
    Nil -> acc
    is Cons -> getTail().product(acc * getHead())
}

tailrec fun <T> FunStream<T>.reverse(acc: FunStream<T> = Nil): FunStream<T> = when (this) {
    Nil -> acc
    is Cons -> getTail().reverse(Cons(this.head) { acc })
}

fun <T> FunStream<T>.appendTail(value: T): FunStream<T> = when (this) {
    Nil -> Cons({ value }) { Nil }
    is Cons -> Cons(this.head) { getTail().appendTail(value) }
}

fun <T> FunStream<T>.appendTailTailrec(value: T): FunStream<T> =
    Cons({ value }) { this.reverse() }.reverse()
